# Donor Scorecard

This scorecard is a decision aid for the donor audit. Scores are 0–5 and describe **design relevance**, not permission to copy source.

Legend:
- **Host** — relevance to launcher/game-host/runtime ownership
- **Core** — module/event/service architecture
- **Render** — HUD/ClickGUI/ESP/render-pipeline relevance
- **Perf** — performance engineering relevance
- **Config** — settings/config/profile design
- **Ext** — scripting/plugins/extensibility
- **Reuse** — current confidence that source can legally be reused in a permissively licensed clean-room project

| Donor | Host | Core | Render | Perf | Config | Ext | Reuse | Current role |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| Drippy Modern | 5 | 4 | 5 | 5 | 3 | 1 | 1 | Primary prior performance/runtime authority; fresh pack still needs static verification |
| Yuri | 5 | 3 | 4 | 3 | 2 | 1 | 4 | Best verified permissive direct-launch 1.8.9 source reference |
| Rise 6.9.5 | 4 | 5 | 5 | 4 | 5 | 4 | 0 | Broad architecture/render/UX reference only |
| OpenOnyx | 4 | 5 | 5 | 4 | 4 | 1 | 0 | Responsibility-boundary and visual reference |
| OpenMyau+ | 2 | 4 | 4 | 3 | 4 | 5 | 1 | Compact Forge/Mixin + scripting reference |
| KRS | 2 | 4 | 5 | 4 | 3 | 2 | 1 | Modern UI/render reference; modern MC baseline |
| FDPClient B17 | 2 | 4 | 5 | 4 | 5 | 3 | 1 | Best typed-config and mature GUI feature reference |
| Raven bS/bS+ | 2 | 3 | 3 | 3 | 3 | 4 | 2 | Lightweight/module/scripting comparison; exact lineage unresolved |
| LibreBounce | 2 | 4 | 3 | 3 | 4 | 2 | 1 | Mature LiquidBounce-style mixin framework reference |
| Vape recovery | 1 | 3 | 2 | 2 | 3 | 2 | 2 | Injection/classloader/verification laboratory |
| OpenAbyss | 2 | 3 | 2 | 2 | 2 | 1 | 0 | Legacy ASM/coremod recovered-source reference |
| OpenExpo | 2 | 2 | 3 | 2 | 2 | 1 | 2 | Related public source is MIT; uploaded identity unresolved |
| Flux-main | 1 | 1 | 1 | 1 | 1 | 1 | 0 | Identity/architecture still unresolved |
| Breeze cracked | 2 | 2 | 3 | 2 | 3 | 3 | 0 | Black-box behavior/UX reference only |
| Timewarp cracked | 1 | 1 | 2 | 1 | 1 | 1 | 0 | Low-priority black-box comparison |
| Slinky cracked | 1 | 2 | 2 | 1 | 2 | 1 | 0 | Injection/behavior reference only |
| AUGUSTUS package | 1 | 2 | 4 | 2 | 3 | 1 | 0 | Visual/behavior comparison only until package identity is verified |
| LeakCryptix | 1 | 2 | 2 | 1 | 2 | 1 | 0 | Recovery/reference only |

## Current leaders by question

**How should the new client own Minecraft 1.8.9?**  
Drippy Modern is the prior performance authority; Yuri is the strongest currently verified permissive direct-launch source comparison.

**How should cross-module state be structured?**  
OpenOnyx and OpenMyau+ are the clearest references. Both make managers/controllers own shared behavior rather than requiring every module to rediscover the same state.

**How should the renderer be structured?**  
Rise provides the strongest staged post-processing model; OpenOnyx provides strong feature boundaries; KRS provides a useful modern ImGui/NanoVG comparison; Drippy provides the prior measured-performance constraints.

**How should settings/configs work?**  
FDP B17 currently leads: typed values, value groups, aliases for renamed settings, descriptions/tooltips and consistent editor support. The useful concepts should be reimplemented independently.

**How should extensibility work?**  
OpenMyau+/Raven provide the most interesting lightweight scripting precedent. A new implementation should expose stable typed logical APIs rather than coupling scripts to obfuscated Minecraft or parsing chat output internally.

## Anti-patterns already rejected

The audit is already strong enough to reject several foundation choices:

- no reflection scan as the permanent module registry;
- no generated event-bus Java source;
- no config file write on every module setter;
- no browser engine as a mandatory ClickGUI dependency;
- no JNI/JVMTI injection layer when the launcher owns process creation;
- no direct dependence on decompiled proprietary code;
- no GPL source copied into a permissively licensed core unless the licensing decision is deliberately changed;
- no committing Mojang/Minecraft source or binaries as project source.

## Gate before architecture freeze

Architecture selection is not final until:

1. the fresh Drippy audit pack can be statically inspected or independently represented by its important source/config files;
2. uploaded-vs-public lineage is resolved where it affects reuse (especially Raven/OpenExpo);
3. archive-only donors are checked for genuinely unique ideas rather than assumed useful because of their names;
4. every selected donor idea has a clean-room note stating what behavior/design is being reproduced without copying protected implementation.
