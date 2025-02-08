package ru.glkot.sexIon;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EmptyChunkGenerator extends ChunkGenerator {

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }
    
    public byte[][] generateBlockSections(World world, Random random, int chunkx, int chunkz, BiomeGrid biomes) {
        return new byte[world.getMaxHeight() / 16][];
    }

}
