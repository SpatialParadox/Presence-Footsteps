package eu.ha3.presencefootsteps.util;

import com.google.gson.stream.JsonWriter;
import dev.architectury.platform.Platform;
import eu.ha3.presencefootsteps.PresenceFootsteps;
import eu.ha3.presencefootsteps.world.Lookup;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class BlockReport {
    private final Path loc;

    public BlockReport(String baseName) {
        loc = getUniqueFileName(Platform.getGameFolder().resolve(PresenceFootsteps.MOD_ID), baseName);
    }

    public CompletableFuture<?> execute(@Nullable Predicate<BlockState> filter) {
        return CompletableFuture.runAsync(() -> {
            try {
                writeReport(filter);
                printResults();
            } catch (Exception e) {
                addMessage(new TranslatableText("pf.report.error", e.getMessage()).styled(s -> s.withColor(Formatting.RED)));
            }
        });
    }

    private void writeReport(@Nullable Predicate<BlockState> filter) throws IOException {
        Files.createDirectories(loc.getParent());

        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(loc))) {
            writer.setIndent("    ");
            writer.beginObject();
            writer.name("blocks");
            writer.beginObject();
            Registry.BLOCK.forEach(block -> {
                BlockState state = block.getDefaultState();

                try {
                    if (filter == null || filter.test(state)) {
                        writer.name(Registry.BLOCK.getId(block).toString());
                        writer.beginObject();
                        writer.name("class");
                        writer.value(getClassData(state));
                        writer.name("sound");
                        writer.value(getSoundData(state));
                        writer.name("association");
                        writer.value(PresenceFootsteps.SOUND_ENGINE.getIsolator().getBlockMap().getAssociation(state, Lookup.EMPTY_SUBSTRATE));
                        writer.endObject();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.endObject();
            writer.name("unmapped_entities");
            writer.beginArray();
            Registry.ENTITY_TYPE.forEach(type -> {
                if (type.create(MinecraftClient.getInstance().world) instanceof LivingEntity) {
                    Identifier id = Registry.ENTITY_TYPE.getId(type);
                    if (!PresenceFootsteps.SOUND_ENGINE.getIsolator().getLocomotionMap().contains(id)) {
                        try {
                            writer.value(id.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            writer.endArray();
            writer.endObject();
        }
    }

    private String getSoundData(BlockState state) {
        if (state.getSoundGroup() == null) {
            return "NULL";
        }
        if (state.getSoundGroup().getStepSound() == null) {
            return "NO_SOUND";
        }
        return state.getSoundGroup().getStepSound().getId().getPath();
    }

    private String getClassData(BlockState state) {
        Block block = state.getBlock();

        String soundName = "";

        if (block instanceof AbstractPressurePlateBlock) soundName += ",EXTENDS_PRESSURE_PLATE";
        if (block instanceof AbstractRailBlock) soundName += ",EXTENDS_RAIL";
        if (block instanceof BlockWithEntity) soundName += ",EXTENDS_CONTAINER";
        if (block instanceof FluidBlock) soundName += ",EXTENDS_LIQUID";
        if (block instanceof PlantBlock) soundName += ",EXTENDS_PLANT";
        if (block instanceof TallPlantBlock) soundName += ",EXTENDS_DOUBLE_PLANT";
        if (block instanceof ConnectingBlock) soundName += ",EXTENDS_CONNECTED_PLANT";
        if (block instanceof LeavesBlock) soundName += ",EXTENDS_LEAVES";
        if (block instanceof SlabBlock) soundName += ",EXTENDS_SLAB";
        if (block instanceof StairsBlock) soundName += ",EXTENDS_STAIRS";
        if (block instanceof SnowyBlock) soundName += ",EXTENDS_SNOWY";
        if (block instanceof SpreadableBlock) soundName += ",EXTENDS_SPREADABLE";
        if (block instanceof FallingBlock) soundName += ",EXTENDS_PHYSICALLY_FALLING";
        if (block instanceof PaneBlock) soundName += ",EXTENDS_PANE";
        if (block instanceof HorizontalFacingBlock) soundName += ",EXTENDS_PILLAR";
        if (block instanceof TorchBlock) soundName += ",EXTENDS_TORCH";
        if (block instanceof CarpetBlock) soundName += ",EXTENDS_CARPET";
        if (block instanceof InfestedBlock) soundName += ",EXTENDS_INFESTED";
        if (block instanceof TransparentBlock) soundName += ",EXTENDS_TRANSPARENT";

        return soundName;
    }

    private void printResults() {
        addMessage(new TranslatableText("pf.report.save")
                .append(new LiteralText(loc.getFileName().toString()).styled(s -> s
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, loc.toString()))
                    .withFormatting(Formatting.UNDERLINE)))
                .styled(s -> s
                    .withColor(Formatting.GREEN)));
    }

    public static void addMessage(Text text) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
    }

    static Path getUniqueFileName(Path directory, String baseName) {
        Path loc = null;

        int counter = 0;
        while (loc == null || Files.exists(loc)) {
            loc = directory.resolve(baseName + (counter == 0 ? "" : "_" + counter) + ".json");
            counter++;
        }

        return loc;
    }
}
