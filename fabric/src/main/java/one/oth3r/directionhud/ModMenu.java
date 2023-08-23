package one.oth3r.directionhud;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Utl;

import java.awt.*;
import java.util.List;

public class ModMenu implements ModMenuApi {
    private static CTxT lang(String key, Object... args) {
        return CUtl.lang("config."+key, args);
    }
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> YetAnotherConfigLib.createBuilder().save(config::save)
                .title(Text.of("DirectionHUD"))
                .category(ConfigCategory.createBuilder()
                        .name(lang("main").b())
                        .tooltip(lang("main.info").b())
                        .option(Option.<Integer>createBuilder()
                                .name(lang("max.xz").b())
                                .description(OptionDescription.of(lang("max.info",lang("max.xz.info").color(CUtl.s())).b()))
                                .binding(config.defaults.MAXxz, () -> config.MAXxz, n -> config.MAXxz = n)
                                .controller(IntegerFieldControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(lang("max.y").b())
                                .description(OptionDescription.of(lang("max.info",lang("max.y.info").color(CUtl.s())).b()))
                                .binding(config.defaults.MAXy, () -> config.MAXy, n -> config.MAXy = n)
                                .controller(IntegerFieldControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(lang("online_mode").b())
                                .description(OptionDescription.of(lang("online_mode.info").b()))
                                .binding(config.defaults.online, () -> config.online, n -> config.online = n)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .group(ListOption.<String>createBuilder()
                                .name(lang("dimensions").b())
                                .description(OptionDescription.of(lang("dimensions.info").append("\n")
                                        .append(lang("dimensions.info_2").color('c')).append("\n\n")
                                        .append(lang("dimensions.info_3",lang("dimensions.info_3.1").color('a'),
                                                lang("dimensions.info_3.2").color('b'),lang("dimensions.info_3.3").color('9'))).append("\n\n")
                                        .append(lang("dimensions.info_4").color('a')).append("\n")
                                        .append(lang("dimensions.info_5").color('b')).append("\n")
                                        .append(lang("dimensions.info_6").color('9')).b()))
                                .binding(config.defaults.dimensions, () -> config.dimensions, n -> config.dimensions = n)
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .build())
                        .group(ListOption.<String>createBuilder()
                                .name(lang("dimension_ratios").b())
                                .description(OptionDescription.of(lang("dimension_ratios.info").append("\n\n")
                                        .append(lang("dimension_ratios.info_2",lang("dimension_ratios.info_2.1").color('a'),
                                                lang("dimension_ratios.info_2.2").color('b'))).b()))
                                .binding(config.defaults.dimensionRatios, () -> config.dimensionRatios, n -> config.dimensionRatios = n)
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(CUtl.lang("dest").b())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("dest_saving").b())
                                        .description(OptionDescription.of(lang("dest_saving.info").b()))
                                        .binding(config.defaults.DESTSaving, () -> config.DESTSaving, n -> config.DESTSaving = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(lang("dest_max_saved").b())
                                        .description(OptionDescription.of(lang("dest_max_saved.info").b()))
                                        .binding(config.defaults.MAXSaved, () -> config.MAXSaved, n -> config.MAXSaved = n)
                                        .controller(IntegerFieldControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("social").b())
                                        .description(OptionDescription.of(lang("social.info").append("\n").append(lang("social.info_2").color('7')).b()))
                                        .binding(config.defaults.social, () -> config.social, n -> config.social = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("death_saving").b())
                                        .description(OptionDescription.of(lang("death_saving.info").b()))
                                        .binding(config.defaults.deathsaving, () -> config.deathsaving, n -> config.deathsaving = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(CUtl.lang("hud").b())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("hud_editing").b())
                                        .description(OptionDescription.of(lang("hud_editing.info").b()))
                                        .binding(config.defaults.HUDEditing, () -> config.HUDEditing, n -> config.HUDEditing = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(lang("hud_refresh").b())
                                        .description(OptionDescription.of(lang("hud_refresh.info").b()))
                                        .binding(config.defaults.HUDRefresh, () -> config.HUDRefresh, n -> config.HUDRefresh = n)
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(1,20))
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(lang("hud").b())
                        .tooltip(lang("hud.info").append("\n").append(lang("defaults.info").color('c')).b())
                        .option(Option.<Boolean>createBuilder()
                                .name(lang("hud.enabled").b())
                                .description(OptionDescription.of(lang("hud.enabled.info").b()))
                                .binding(config.defaults.HUDEnabled, () -> config.HUDEnabled, n -> config.HUDEnabled = n)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .group(ListOption.<String>createBuilder()
                                .name(lang("hud.order").b())
                                .description(OptionDescription.of(lang("hud.order.info").b()))
                                .binding(List.of(config.defaults.HUDOrder.split(" ")),
                                        () -> List.of(config.HUDOrder.split(" ")), n -> config.HUDOrder = String.join(" ", n))
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(lang("hud.module").b())
                                .description(OptionDescription.of(lang("hud.module.info").b()))
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("hud.module.coordinates").b())
                                        .binding(config.defaults.HUDCoordinates, () -> config.HUDCoordinates, n -> config.HUDCoordinates = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("hud.module.destination").b())
                                        .binding(config.defaults.HUDDestination, () -> config.HUDDestination, n -> config.HUDDestination = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("hud.module.distance").b())
                                        .binding(config.defaults.HUDDistance, () -> config.HUDDistance, n -> config.HUDDistance = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("hud.module.tracking").b())
                                        .binding(config.defaults.HUDTracking, () -> config.HUDTracking, n -> config.HUDTracking = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("hud.module.direction").b())
                                        .binding(config.defaults.HUDDirection, () -> config.HUDDirection, n -> config.HUDDirection = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("hud.module.time").b())
                                        .binding(config.defaults.HUDTime, () -> config.HUDTime, n -> config.HUDTime = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("hud.module.weather").b())
                                        .binding(config.defaults.HUDWeather, () -> config.HUDWeather, n -> config.HUDWeather = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(lang("hud.settings").b())
                                .description(OptionDescription.of(lang("hud.settings.info").b()))
                                .option(Option.<config.HUDTypes>createBuilder()
                                        .name(lang("hud.settings.type").b())
                                        .binding(config.HUDTypes.get(config.defaults.HUDType), () -> config.HUDTypes.get(config.HUDType), n -> config.HUDType = n.toString())
                                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(config.HUDTypes.class)
                                                .valueFormatter(v -> CUtl.lang("hud.settings.type."+v.name().toLowerCase()).b()))
                                        .build())
                                .option(Option.<config.BarColors>createBuilder()
                                        .name(lang("hud.settings.bossbar.color").b())
                                        .binding(config.BarColors.get(config.defaults.HUDBarColor), () -> config.BarColors.get(config.HUDBarColor), n -> config.HUDBarColor = n.toString())
                                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(config.BarColors.class)
                                                .valueFormatter(v -> CUtl.lang("hud.settings.bossbar.color."+v.name().toLowerCase()).color(Assets.barColor(v)).b()))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("hud.settings.bossbar.distance").b())
                                        .binding(config.defaults.HUDBarShowDistance, () -> config.HUDBarShowDistance, n -> config.HUDBarShowDistance = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Long>createBuilder()
                                        .name(lang("hud.settings.bossbar.distance_max").b())
                                        .description(OptionDescription.of(lang("hud.settings.bossbar.distance_max.info").b()))
                                        .binding(config.defaults.HUDBarDistanceMax, () -> config.HUDBarDistanceMax, n -> config.HUDBarDistanceMax = n)
                                        .controller(LongFieldControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("hud.settings.module.time_24hr").b())
                                        .binding(config.defaults.HUDTime24HR, () -> config.HUDTime24HR, n -> config.HUDTime24HR = n)
                                        .controller(opt -> BooleanControllerBuilder.create(opt).trueFalseFormatter())
                                        .build())
                                .option(Option.<config.HUDTrackingTargets>createBuilder()
                                        .name(lang("hud.settings.module.tracking_target").b())
                                        .binding(config.HUDTrackingTargets.get(config.defaults.HUDBarColor), () -> config.HUDTrackingTargets.get(config.HUDBarColor), n -> config.HUDBarColor = n.toString())
                                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(config.HUDTrackingTargets.class)
                                                .valueFormatter(v -> CUtl.lang("hud.settings.module.tracking_target."+v.name().toLowerCase()).b()))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(lang("hud.color.category",Utl.capitalizeFirst(CUtl.lang("hud.color.primary").getString())).b())
                                .description(OptionDescription.of(lang("hud.color.category.info",CUtl.lang("hud.color.primary")).b()))
                                .option(Option.<Color>createBuilder()
                                        .name(lang("hud.color").b())
                                        .binding(Color.decode(CUtl.color.format(config.defaults.HUDPrimaryColor)),() -> Color.decode(CUtl.color.format(config.HUDPrimaryColor)),
                                                n -> config.HUDPrimaryColor = String.format("#%02x%02x%02x", n.getRed(), n.getGreen(), n.getBlue()))
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("hud.color.bold").b())
                                        .binding(config.defaults.HUDPrimaryBold, () -> config.HUDPrimaryBold, n -> config.HUDPrimaryBold = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("hud.color.italics").b())
                                        .binding(config.defaults.HUDPrimaryItalics, () -> config.HUDPrimaryItalics, n -> config.HUDPrimaryItalics = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("hud.color.rainbow").b())
                                        .binding(config.defaults.HUDPrimaryRainbow, () -> config.HUDPrimaryRainbow, n -> config.HUDPrimaryRainbow = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(lang("hud.color.category",Utl.capitalizeFirst(CUtl.lang("hud.color.secondary").getString())).b())
                                .description(OptionDescription.of(lang("hud.color.category.info",CUtl.lang("hud.color.secondary")).b()))
                                .option(Option.<Color>createBuilder()
                                        .name(lang("hud.color").b())
                                        .binding(Color.decode(CUtl.color.format(config.defaults.HUDSecondaryColor)),() -> Color.decode(CUtl.color.format(config.HUDSecondaryColor)),
                                                n -> config.HUDSecondaryColor = String.format("#%02x%02x%02x", n.getRed(), n.getGreen(), n.getBlue()))
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("hud.color.bold").b())
                                        .binding(config.defaults.HUDSecondaryBold, () -> config.HUDSecondaryBold, n -> config.HUDSecondaryBold = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("hud.color.italics").b())
                                        .binding(config.defaults.HUDSecondaryItalics, () -> config.HUDSecondaryItalics, n -> config.HUDSecondaryItalics = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(lang("hud.color.rainbow").b())
                                        .binding(config.defaults.HUDSecondaryRainbow, () -> config.HUDSecondaryRainbow, n -> config.HUDSecondaryRainbow = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(lang("dest").b())
                        .tooltip(lang("dest.info").append("\n").append(lang("defaults.info").color('c')).b())
                        .group(OptionGroup.createBuilder()
                                .name(lang("dest.settings").b())
                                .description(OptionDescription.of(lang("dest.settings.info").b()))
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("dest.settings.autoclear").b())
                                        .binding(config.defaults.DESTAutoClear, () -> config.DESTAutoClear, n -> config.DESTAutoClear = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Long>createBuilder()
                                        .name(lang("dest.settings.autoclear_rad").b())
                                        .binding(config.defaults.DESTAutoClearRad, () -> config.DESTAutoClearRad, n -> config.DESTAutoClearRad = n)
                                        .controller(opt -> LongSliderControllerBuilder.create(opt).step(1L).range(1L, 15L))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("dest.settings.autoconvert").b())
                                        .binding(config.defaults.DESTAutoConvert, () -> config.DESTAutoConvert, n -> config.DESTAutoConvert = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("dest.settings.ylevel").b())
                                        .binding(config.defaults.DESTYLevel, () -> config.DESTYLevel, n -> config.DESTYLevel = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(lang("dest.settings.particles").b())
                                .description(OptionDescription.of(lang("dest.settings.particles.info").b()))
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("dest.settings.particles.dest").b())
                                        .binding(config.defaults.DESTDestParticles, () -> config.DESTDestParticles, n -> config.DESTDestParticles = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(lang("dest.settings.particles.color",CUtl.lang("dest.settings.particles.dest")).b())
                                        .binding(Color.decode(CUtl.color.format(config.defaults.DESTDestParticleColor)),() -> Color.decode(CUtl.color.format(config.DESTDestParticleColor)),
                                                n -> config.DESTDestParticleColor = String.format("#%02x%02x%02x", n.getRed(), n.getGreen(), n.getBlue()))
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("dest.settings.particles.line").b())
                                        .binding(config.defaults.DESTLineParticles, () -> config.DESTLineParticles, n -> config.DESTLineParticles = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(lang("dest.settings.particles.color",CUtl.lang("dest.settings.particles.line")).b())
                                        .binding(Color.decode(CUtl.color.format(config.defaults.DESTLineParticleColor)),() -> Color.decode(CUtl.color.format(config.DESTLineParticleColor)),
                                                n -> config.DESTLineParticleColor = String.format("#%02x%02x%02x", n.getRed(), n.getGreen(), n.getBlue()))
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("dest.settings.particles.tracking").b())
                                        .binding(config.defaults.DESTTrackingParticles, () -> config.DESTTrackingParticles, n -> config.DESTTrackingParticles = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(lang("dest.settings.particles.color",CUtl.lang("dest.settings.particles.tracking")).b())
                                        .binding(Color.decode(CUtl.color.format(config.defaults.DESTTrackingParticleColor)),() -> Color.decode(CUtl.color.format(config.DESTTrackingParticleColor)),
                                                n -> config.DESTTrackingParticleColor = String.format("#%02x%02x%02x", n.getRed(), n.getGreen(), n.getBlue()))
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(lang("dest.settings.features").b())
                                .description(OptionDescription.of(lang("dest.settings.features.info").b()))
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("dest.settings.features.send").b())
                                        .binding(config.defaults.DESTSend, () -> config.DESTSend, n -> config.DESTSend = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("dest.settings.features.track").b())
                                        .binding(config.defaults.DESTTrack, () -> config.DESTTrack, n -> config.DESTTrack = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<config.DESTTrackingRequestModes>createBuilder()
                                        .name(lang("dest.settings.features.track_request_mode").b())
                                        .description(OptionDescription.of(lang("dest.settings.features.track_request_mode.info",
                                                CUtl.lang("dest.settings.features.track_request_mode.instant").color(CUtl.s()),CUtl.lang("dest.settings.features.track_request_mode.instant.info")).append("\n")
                                                .append(lang("dest.settings.features.track_request_mode.info",
                                                        CUtl.lang("dest.settings.features.track_request_mode.request").color(CUtl.s()),CUtl.lang("dest.settings.features.track_request_mode.request.info"))).b()))
                                        .binding(config.DESTTrackingRequestModes.get(config.defaults.DESTTrackingRequestMode), () -> config.DESTTrackingRequestModes.get(config.DESTTrackingRequestMode), n -> config.DESTTrackingRequestMode = n.toString())
                                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(config.DESTTrackingRequestModes.class)
                                                .valueFormatter(v -> CUtl.lang("dest.settings.features.track_request_mode."+v.name().toLowerCase()).b()))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(CUtl.lang("dest.settings.features.lastdeath").b())
                                        .binding(config.defaults.DESTLastdeath, () -> config.DESTLastdeath, n -> config.DESTLastdeath = n)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .build().generateScreen(parent);
    }
}
