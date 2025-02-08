package ru.glkot.sexIon.messaging;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.glkot.sexIon.SexIon;
import ru.glkot.sexIon.Sxlib;
import ru.glkot.sexIon.playerdata.PlayerData;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class Receiver {
    public static void startServer(Sxlib sxlib) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(Bukkit.getPort()+1000)) { // Слушаем порт 8842
                getLogger().info("Сокет-сервер Paper запущен на порту 8842");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String message = reader.readLine();
                    getLogger().info("Получено сообщение от Proxy: " + message);
                    obrabotka(message,sxlib);
                }
            } catch (Exception e) {
                getLogger().severe("Ошибка сокет-сервера Paper: " + e.getMessage());
            }
        }).start();
    }

    public static void sendMessageToProxy(String message) {

        try (Socket socket = new Socket(Sxlib.getCon().getString("veslo-ip"), 8841); // Подключаемся к Velocity
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            writer.println(message);
            getLogger().info("Сообщение отправлено Proxy: " + message);
        } catch (Exception e) {
            getLogger().severe("Ошибка при отправке сообщения: " + e.getMessage());
        }
    }

    public static void sendAllSexions(Sxlib sxlib) {
        sendAllSexions(sxlib.id.toString(),sxlib.config.getString("server-type"));
    }
    public static void sendAllSexions(String id,String serverType) {
        JSONObject object = new JSONObject();
        object.put("type", "sexions");

        JSONArray sexionsArray = new JSONArray(); // Создаем JSON-массив
        List<SexIon> list = SexIon.getAll();
        for (SexIon sexIon : list) {
            sexIon.putUnFlush("server", id);
            if (sexIon.generated() && sexIon.world() != null) sexIon.putUnFlush("player-count", sexIon.world().getPlayerCount());
            else {
                sexIon.putUnFlush("player-count", 0);
                sexIon.generate();
            }
            sexIon.putUnFlush("server-ip", Bukkit.getServer().getIp()+":"+Bukkit.getServer().getPort());
            sexionsArray.put(sexIon.getData()); // Добавляем объект напрямую
        }

        JSONObject data = new JSONObject();
        data.put("sexions", sexionsArray); // Используем JSONArray вместо списка строк
        data.put("id", id);
        data.put("type", serverType);

        object.put("data", data);

        sendMessageToProxy(object.toString());
    }

    public static void obrabotka (String message, Sxlib sxlib) {
        JSONObject object = new JSONObject(message);
        String type = object.getString("type");
        switch (type) {
            case "update" -> {
                sxlib.genId();
                sendAllSexions(sxlib);
            }
            case "playerData" -> {
                JSONObject data = object.getJSONObject("data"); new PlayerData(data);
            }
            case "createNewSexion" -> {
                new SexIon(UUID.randomUUID());
            }
            case "rpHash" -> {
                File file = Path.of(Path.of("").toAbsolutePath().toString(),"rp.hash").toFile();
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(object.getString("data"));
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "listTransfer" -> {
                JSONObject data = object.getJSONObject("data");
                try (FileWriter fileWriter = new FileWriter(Path.of(data.getString("fileName")).toFile())) {
                    fileWriter.write(data.getString("list"));
                    fileWriter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public static void receiveData(String messageType, @Nullable Object data) {
        JSONObject object = new JSONObject();
        object.put("type",messageType);
        if (data != null) {
            object.put("data",data);
        }

        sendMessageToProxy(object.toString());
    }
    public static void receiveID(String messageType, String id, String serverType) {
        JSONObject object = new JSONObject();
        object.put("type",messageType);
        JSONObject data = new JSONObject();
        data.put("id",id);
        data.put("ip",Bukkit.getServer().getIp()+":"+Bukkit.getServer().getPort());
        data.put("serverType",serverType);
        data.put("sex-count",SexIon.getCount());
        if (Bukkit.getOnlinePlayers().isEmpty()) data.put("player-count",0);
        else data.put("player-count",Bukkit.getOnlinePlayers().size());
        if (!SexIon.getFreebies().isEmpty())data.put("sex-freebies",SexIon.getFreebies().size());
        else data.put("sex-freebies",0);
        data.put("canCreateSex",SexIon.getCount()<Sxlib.getCon().getInt("max-sex"));
        object.put("data",data);

        sendMessageToProxy(object.toString());
    }
}
