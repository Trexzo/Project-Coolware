# Trexzo Cleanroom Minecraft Client

This directory is an incubator for a brand-new Minecraft client/launcher implementation.

## Non-negotiable rules

- New implementation, not a fork of Coolware or another donor client.
- Donor projects are references for architecture, UX, performance ideas, and behavior comparison.
- Do not copy closed-source/cracked/proprietary implementation code or assets.
- Direct code reuse requires a compatible license and attribution review first.
- Minecraft/Mojang binaries and source are not committed to this tree.
- Build and CI must be reproducible from GitHub without relying on the developer PC.
- Java 21 is the host/runtime target unless a Minecraft 1.8.9 compatibility boundary requires isolation.
- Performance work is measurement-led: no unsupported FPS claims.

## Initial architecture direction

1. **Bootstrap / launcher** — runtime discovery, profiles, JVM options, game directory ownership, update manifest.
2. **Client core** — lifecycle, event bus, module registry, settings, commands, config/profile persistence.
3. **Platform boundary** — explicit Minecraft 1.8.9 adapters instead of leaking game classes through every module.
4. **Render layer** — immutable frame snapshots, cached text/layout, batching, explicit GL state ownership, no hot-path readbacks.
5. **Feature modules** — isolated capability packages with declared event/render dependencies.
6. **Diagnostics** — frame timings, event timings, allocation counters, render counters, safe debug overlays.

## Donor/reference priorities

- Coolware Modern: standalone Java 21/performance direction, snapshotting/caching/batching lessons.
- Coolware legacy: broad module/settings UX behavior reference only.
- OpenMyau+: ClickGUI/HUD/module UX and selected subsystem ideas.
- Onyx/OpenOnyx: rendering/visual behavior reference.
- Rise: performance/UI architecture reference where source evidence is available.
- FDP/Raven/Yuri/KRS/Flux/OpenAbyss/OpenExpo/LibreBounce: targeted subsystem references.
- Vape/Slinky/Augustus/cracked or binary-only material: black-box UX/behavior reference only unless compatible source licensing is established.

## Milestones

- M0: deterministic Java 21 bootstrap + CI.
- M1: core event/module/settings/config architecture with tests.
- M2: launcher profile/update/runtime model.
- M3: Minecraft 1.8.9 platform bridge and legal dependency acquisition strategy.
- M4: render/HUD/ClickGUI foundation.
- M5: first real modules + profiler-driven optimization.
- M6: packaging, updater, release manifest, end-user installer.
