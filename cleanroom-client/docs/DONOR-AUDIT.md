# Donor Audit

Status: **audit gate in progress**  
Scope: clean-room Minecraft client / launcher incubator  
Rule: this document records donor observations only. It does **not** authorize copying code.

## Audit policy

The target project is a new implementation. Donor material is classified into four buckets:

1. **Potentially reusable source** — only where the specific source lineage and license are compatible.
2. **Copyleft source reference** — architecture/behavior can be studied, but direct reuse would carry license obligations.
3. **Recovered/decompiled source reference** — useful for understanding responsibilities and behavior, not a clean source authority.
4. **Binary/cracked reference** — behavior, UX and performance observations only; no code/assets are to be copied.

A public repository that appears related to an uploaded archive is **not assumed identical** unless lineage/hash equivalence is established.

## Current donor matrix

| Donor family | Evidence available | Runtime / framework | License / provenance | High-value lessons | Reuse posture |
|---|---|---|---|---|---|
| **Drippy Modern / late Coolware Modern** | Uploaded `Drippy-Modern-AUDIT.zip`; prior project authority exists, but the fresh ZIP could not be unpacked by the current chat archive backend | Prior authority: standalone/direct-host 1.8.9 modernization rather than normal Forge runtime | Mixed lineage; fresh pack not yet provenance-verified | Performance instrumentation, hot-path ownership, renderer batching/caching, direct-host migration, launcher/runtime separation | **Reference until fresh audit pack is readable** |
| **VapeV4.21 recovery** | Uploaded clean-source ZIP + connected `Trexzo/VapeV4.21` | Java payload + Windows x64 native/JVMTI/JNI injection; ASM/Javassist; cross-version bridge | Recovery repo uses CC0 only to the extent contributors actually own the material; README explicitly says it is not official Vape source | Classloader isolation, relocated bytecode tooling, injection payload verification, Java 8 compatibility checks | **Research/reference; only contributor-owned original recovery glue is even a possible reuse candidate** |
| **OpenMyau+** | Uploaded source + runtime JAR + connected `Trexzo/OpenMyau-Plus` | Forge 1.8.9, Java 8, Mixin 0.7.11, access transformer, Essential/Architectury Loom | GPLv3 | Compact Forge/Mixin build, one-JAR packaging, ViaVersion family integration, command-driven scripting bridge, explicit upstream provenance for vendored RSL | **Copyleft reference unless target project intentionally becomes GPL** |
| **Myau+ compiled runtime** | Uploaded `Myau+.jar-2.1+4.jar` | Compiled 1.8.9 Forge client | Follows OpenMyau+ lineage but exact binary parity not yet statically verified | Source-vs-runtime parity target, packaging footprint | **Binary parity/reference only until static comparison is possible** |
| **OpenOnyx / deobfuscated Onyx** | Uploaded OpenOnyx source ZIP + deobfuscated archive; public OpenOnyx recovery project located | Supplied JDK 21 runtime with recovered 1.8.9 client; source launcher separate from recovered source tree | Recovered/decompiled proprietary lineage; source tree is explicitly decompiler output | Excellent responsibility boundaries: combat controller, aim controller, rotation manager, target filters, typed settings, render/HUD separation | **Reference only** |
| **Rise 6.9.5** | Uploaded archive + public recovered/deobfuscated source ecosystem | Standalone recovered client; JDK 21-era workspace; centralized managers and shader render manager | Recovered/decompiled proprietary client; source contains proprietary copyright notices | Strongest broad architecture reference: event/module/component/command/config/theme/script/keybind managers, staged shader rendering, dual ClickGUI approaches, async tasks | **Reference only** |
| **Yuri** | Uploaded source + connected `Trexzo/Yuri` | Direct `net.minecraft.client.main.Main` 1.8.9 launch, ShadowJar, LWJGL2, ImGui bindings; bundled Java 8 distribution | MIT at repo level, but README acknowledges snippets from other clients | Direct-launch packaging, minimal standalone distribution, ImGui-on-LWJGL2 integration | **Potentially reusable only file-by-file after lineage check** |
| **KRS** | Uploaded source + connected `Trexzo/Krs` | Minecraft 26.2, Fabric, Java 25, ImGui + NanoVG + STB | GPLv3 | Modern UI/render stack, access widener, split environment source sets, modern Fabric packaging | **Copyleft reference** |
| **FDPClient B17** | Uploaded B17 archive + public official project/release notes | Forge 1.8.9, Mixin, Java/Kotlin; LiquidBounce-derived | GPLv3 | Very rich typed setting system, rename-safe config aliases, UI search, glyph caching, theme unification, persistent pre-warmed web ClickGUI, async asset delivery | **Copyleft reference; adapt ideas, not code** |
| **LibreBounce** | Uploaded source + connected `Trexzo/LibreBounce` | Forge 1.8.9, Java/Kotlin JVM8, Mixin/coremod, LiquidBounce Legacy lineage | GPLv3 | Mature mixin injection, refactored LiquidBounce-style framework, explicit separation from Mojang source, config/event/module organization | **Copyleft reference** |
| **Raven bS / bS+** | Uploaded bS JAR + bS+ source archive; related public RavenBS++ project located | Forge 1.8.9; public related project builds with JDK 21 | Related RavenBS++ project is MIT, but exact uploaded bS/bS+ lineage has not been proven identical | Lightweight module/client patterns and scripting ecosystem; useful contrast to heavier frameworks | **Potentially reusable only after exact lineage/license verification; otherwise reference** |
| **OpenAbyss** | Uploaded source + connected `Trexzo/OpenAbyss` | Forge 1.8.9, Java 8, LaunchWrapper, ASM coremod | Recovered/decrypted source; no top-level license found in connected repo audit | Coremod/ASM mechanics, recovered semantics, legacy client structure | **Reference only** |
| **OpenExpo** | Uploaded binary archive; related public `NoHackClient/OpenExpo` exists | Exact uploaded runtime not yet inspected | Public related project is MIT; exact uploaded archive identity not proven | Possible additional open 1.8.9 implementation reference | **Do not reuse until exact lineage is established** |
| **Flux-main** | Uploaded large source archive + connected `Trexzo/Flux` with minimal README | Exact architecture not yet established from accessible source | License not established in connected repo pass | Secondary comparison donor | **Reference pending verification** |
| **Breeze** | Uploaded `Breeze.jar hackvshack.net.zip` | Exact version/runtime not statically inspected | Cracked/binary provenance | UX, scripting/config behavior may be worth observing | **Black-box reference only** |
| **Timewarp** | Uploaded cracked archive | Exact runtime not statically inspected | Cracked/binary provenance | Only use if it demonstrates a genuinely unique behavior/UX idea | **Black-box reference only** |
| **Slinky cracked** | Uploaded source/crack archive | Injection/client lineage appears crack-oriented; exact archive not unpacked | Untrusted proprietary/cracked provenance | Injection UX/behavior comparison only | **Reference only** |
| **AUGUSTUS** | Uploaded ~440 MB package; public AugustusClient ecosystem exists but identity is not proven | Exact uploaded package not statically inspected | Proprietary/package lineage not verified | UI/visual/behavior comparison if unique | **Reference only** |
| **LeakCryptix** | Uploaded small recovered-source archive | Exact internals not unpacked by current backend | Recovery/crack provenance; no trusted license established | Targeted behavior/implementation comparison only | **Reference only** |

## Verified architectural observations

### OpenMyau+

The build is intentionally compact: Forge 1.8.9 + stable MCP mappings + Mixin + optional access transformer. The root build also compiles the vendored Raven Script Loader source/resources, producing one jar with two Forge mod identities and two mixin configurations.

The scripting bridge is notable because RSL does not need a compile-time dependency on the Myau implementation. Scripts issue Myau chat commands and consume suppressed command responses. That loose coupling is worth preserving conceptually, although a new client should expose a typed internal API rather than parsing its own chat text.

### Yuri

Yuri is the strongest currently verified direct-launch source donor. Its Gradle application entry is Minecraft's `net.minecraft.client.main.Main`, and its dependency list reconstructs the needed 1.8.9 runtime libraries directly rather than making Forge the client shell.

That is architecturally closer to the late Drippy direction than the Forge-based donors. Yuri also demonstrates ImGui bindings on the LWJGL2 generation used by 1.8.9.

### KRS

KRS is intentionally a modern-generation comparison point rather than a 1.8.9 host donor: its connected source targets Minecraft 26.2, Fabric Loader 0.19.3 and Java 25. It combines ImGui with NanoVG and STB font support.

The useful lesson is renderer/UI composition: immediate-mode controls can coexist with a vector rendering path and dedicated font stack. We should not import its GPL code into a non-GPL core.

### Legacy Coolware

Legacy Coolware is valuable historically but its core plumbing should not become the new foundation:

- module discovery uses runtime reflection,
- module settings are reflected fields,
- module lifecycle directly triggers config writes and notifications,
- command discovery is reflective,
- the custom event bus is generated from source scanning in `build.gradle`.

The new client can preserve the familiar module/category/keybind UX while using explicit registries, deterministic lifecycle ownership and a separate persistence layer.

### Rise 6.9.5

Recovered Rise shows a broad manager-oriented shell: event bus, module manager, component manager, command manager, file/config/data managers, theme manager, script manager, keybind manager, bot manager, cheat detector and shader render manager.

Its GUI/render path also separates normal UI rendering from bloom/blur/post-processing stages. That separation is a strong design reference for a performant renderer, but the recovered proprietary source is not a code donor.

### OpenOnyx

OpenOnyx is particularly useful for responsibility boundaries. The recovered source assigns distinct owners for rotation, aiming and combat orchestration rather than putting everything into one module class. Its setting layer includes boolean, number, enum and multi-select types.

For the new client, the architectural lesson is to keep targeting/rotation/combat state as shared services with modules consuming them, while ensuring services have explicit ownership and deterministic tick/render snapshots.

### FDPClient B17

B17 is the strongest configuration/UI feature reference in the current set. Its release introduces typed values such as multi-select, keybind, vector, file and curve values; rename-safe aliases; tooltips; scoped search; shared themes and on-demand glyph caching.

Its embedded-browser GUI is interesting UX research but is not the default direction for the clean-room client: a native renderer avoids the browser footprint and keeps frame pacing under our control.

### Vape recovery

The Vape recovery project is valuable as an interop laboratory, not as the target architecture. It validates an injection payload, shades/relocates ASM and Javassist to survive hostile classloader boundaries, can emit Java 8-compatible bytecode, and has a native bridge for loading into existing JVMs.

A from-scratch launcher/client does not need that complexity if it owns the launch process, but the classloader and verification lessons are useful for any future plugin/compatibility layer.

## Candidate lessons — not architecture decisions yet

The audit currently favors these ideas:

- **Standalone/direct-host ownership** from late Drippy plus Yuri, rather than making legacy Forge the permanent shell.
- **Explicit service and module registries**, not runtime reflection or generated event-bus source.
- **Typed settings with stable IDs and aliases**, inspired by FDP's mature value system.
- **Separated render stages** for normal UI, blur, bloom and other post effects, inspired by Rise but implemented independently.
- **Shared immutable frame/tick snapshots** so HUD/ESP modules do not repeatedly scan world state.
- **Native UI rendering** as the default; web/browser UI is a reference, not the base dependency.
- **A clean scripting/plugin boundary** modeled conceptually on Myau+/Raven's loose coupling, but with a typed API.
- **Modern UI experiments** can borrow concepts from KRS's ImGui/NanoVG composition without importing GPL implementation.
- **Direct-launch packaging** should be tested against Yuri and Drippy behavior before choosing the final 1.8.9 host strategy.

## Audit blockers / uncertainty

The current chat archive backend is registering all uploaded ZIP/JAR files but is failing to expose ZIP internals for direct static extraction. Therefore:

- no claim is made that a public repo is byte-for-byte identical to an uploaded archive;
- no claim is made about an archive-only donor's exact module list or license unless separately source-backed;
- none of the uploaded binaries have been executed;
- Drippy's fresh audit pack still needs exact static verification before its late runtime state is treated as fresh authority.

This uncertainty must be resolved before the donor audit is marked complete and before donor-specific implementation is copied or translated.
