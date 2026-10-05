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
| **VapeV4.21 recovery** | Uploaded clean-source ZIP + connected `Trexzo/VapeV4.21` | Java payload + Windows x64 JNI/JVMTI bridge; JDK 17 build toolchain; payload can be compiled to Java 8 bytecode; ASM/Javassist are shaded into client namespace | Recovery repo uses CC0 only to the extent contributors actually own the material; README explicitly says it is not official Vape source | Classloader isolation, dependency shading, payload completeness checks, Java-version verification, cross-version mapping boundaries | **Research/reference; only clearly contributor-owned recovery glue is even a possible reuse candidate** |
| **OpenMyau+** | Uploaded source + runtime JAR + connected `Trexzo/OpenMyau-Plus` | Forge 1.8.9, Java 8, Mixin 0.7.11, access transformer, Essential/Architectury Loom | GPLv3 | Compact Forge/Mixin build, one-JAR packaging, ViaVersion family integration, command-driven scripting bridge, explicit upstream provenance for vendored RSL | **Copyleft reference unless target project intentionally becomes GPL** |
| **Myau+ compiled runtime** | Uploaded `Myau+.jar-2.1+4.jar` | Compiled 1.8.9 Forge client | Follows OpenMyau+ lineage but exact binary parity not yet statically verified | Source-vs-runtime parity target, packaging footprint | **Binary parity/reference only until static comparison is possible** |
| **OpenOnyx / deobfuscated Onyx** | Uploaded OpenOnyx source ZIP + deobfuscated archive; public OpenOnyx recovery project located | Supplied JDK 21 runtime with recovered 1.8.9 client; source launcher separate from recovered source tree | Recovered/decompiled proprietary lineage; source tree is explicitly decompiler output | Excellent responsibility boundaries: combat controller, aim controller, rotation manager, target filters, typed settings, render/HUD separation | **Reference only** |
| **Rise 6.9.5** | Uploaded archive + public recovered/deobfuscated source ecosystem | Standalone recovered client; JDK 21-era workspace; centralized managers and shader render manager | Recovered/decompiled proprietary client; source contains proprietary copyright notices | Strong broad architecture reference: event/module/component/command/config/theme/script/keybind managers, staged shader rendering, dual ClickGUI approaches, async tasks | **Reference only** |
| **Yuri** | Uploaded source + connected `Trexzo/Yuri` | Direct `net.minecraft.client.main.Main` 1.8.9 launch, ShadowJar, LWJGL2, ImGui bindings; bundled Java 8 distribution | Repository says MIT, but README acknowledges snippets from other clients **and the tree contains Minecraft/Mojang source such as `net.minecraft.client.main.Main`** | Direct-launch shape, legacy runtime dependency set, minimal distribution, ImGui-on-LWJGL2 integration | **Architecture/reference only by default; any original file would require file-level provenance review** |
| **KRS** | Uploaded source + connected `Trexzo/Krs` | Minecraft 26.2, Fabric Loader 0.19.3, Java 25, ImGui + NanoVG + STB | GPLv3 | Modern UI/render stack, access widener, split environment source sets, fat-jar library handling | **Copyleft reference** |
| **FDPClient B17** | Uploaded B17 archive + public official project/release notes | Forge 1.8.9, Mixin, Java/Kotlin; LiquidBounce-derived | GPLv3 | Very rich typed setting system, rename-safe config aliases, UI search, glyph caching, theme unification, persistent pre-warmed web ClickGUI, async asset delivery | **Copyleft reference; adapt ideas, not code** |
| **LibreBounce** | Uploaded source + connected `Trexzo/LibreBounce` | Forge 1.8.9, Java/Kotlin JVM8, Mixin/coremod, LiquidBounce Legacy lineage | GPLv3; README additionally warns that development/compilation may involve source to which the project has no rights | Mature mixin injection, refactored LiquidBounce-style framework, explicit separation from Mojang source, config/event/module organization | **Copyleft/reference only; extra provenance caution beyond GPL itself** |
| **Raven bS / bS+** | Uploaded bS JAR + bS+ source archive; related public RavenBS++ project located | Forge 1.8.9; public related project builds with JDK 21 | Related RavenBS++ project is MIT, but exact uploaded bS/bS+ lineage has not been proven identical | Lightweight module/client patterns and scripting ecosystem; useful contrast to heavier frameworks | **Potentially reusable only after exact lineage/license verification; otherwise reference** |
| **OpenAbyss** | Uploaded source + connected `Trexzo/OpenAbyss` | Java 8, LaunchWrapper, ASM coremod; manifest declares `Abyss.ASM.CoreMod`; build uses compile-only legacy MC/Forge libraries | Recovered/decrypted source; no top-level license found in connected repo | Coremod/ASM mechanics, recovered semantics, legacy launch structure | **Reference only** |
| **OpenExpo** | Uploaded binary archive; connected `Trexzo/OpenExpo` currently exposes no verified root build/license metadata in this audit | Exact uploaded runtime not yet inspected | Exact uploaded archive identity/license not proven | Possible additional implementation reference if unique behavior is demonstrated | **Reference only until exact lineage is established** |
| **Legacy Coolware / Project-Coolware** | Connected `Trexzo/Project-Coolware` + historical source/packages | Forge 1.8.9, Java 8, ForgeGradle 2.1, Mixin 0.7.11, reflection-based discovery, generated event-bus source | No top-level license found in connected repo | Historical UX/module behavior; examples of what the new foundation should simplify | **Reference only unless individual provenance is established** |
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

Yuri is the strongest currently verified direct-launch source donor. Its Gradle application entry and final JAR manifest both point directly at Minecraft's `net.minecraft.client.main.Main`, and the project declares the legacy 1.8.9 runtime libraries itself instead of using Forge as the shell.

That is architecturally close to the late Drippy direction and therefore highly relevant to the clean-room host boundary. Yuri also demonstrates ImGui bindings on LWJGL2.

The MIT license makes Yuri more permissive than most donors, but the README's statement that snippets from other hacked clients were used means **MIT at repository level is not enough to treat every file as clean-origin source**. Any code-level reuse would need a file-by-file provenance pass.

### KRS

KRS is intentionally a modern-generation comparison point rather than a 1.8.9 host donor. Its connected source targets Minecraft 26.2, Fabric Loader 0.19.3 and Java 25, with split environment source sets and an access widener.

Its UI stack combines ImGui, NanoVG and STB. That makes it useful for studying modern renderer/UI composition, but GPLv3 means implementation should not be copied into a permissively licensed core.

### LibreBounce

LibreBounce is a Forge 1.8.9 / Java-Kotlin / Mixin coremod fork in the LiquidBounce Legacy family. Its README explicitly explains the advantage of injection over shipping Mojang source.

It also contains an unusually important provenance warning: the GPL applies to source directly in the clean repository, while additional source may be used during development/compilation to which the maintainers state they have no rights. For the clean-room project this means LibreBounce is a **concept donor only**, even beyond the normal GPL restriction.

### Legacy Coolware

Legacy Coolware is valuable historically but its core plumbing should not become the new foundation:

- ForgeGradle 2.1 / Java 8 is the permanent shell,
- module and command discovery use runtime reflection,
- module settings are reflected fields,
- module lifecycle directly triggers config writes and notifications,
- the custom event bus is generated by scanning source from `build.gradle`,
- the connected repository has no top-level license.

The new client can preserve familiar module/category/keybind UX while using explicit registries, deterministic lifecycle ownership and a separate persistence layer.

### OpenAbyss

OpenAbyss is a recovered/decrypted Java 8 project using LaunchWrapper and an ASM coremod rather than a clean modern injection boundary. Its build manifest declares `Abyss.ASM.CoreMod` and the build expects legacy Minecraft/Forge libraries as compile-only inputs.

This makes it useful for understanding old transformation responsibilities, but not attractive as a foundation. The absence of a repository license further keeps it firmly in reference-only territory.

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

The Vape recovery project is valuable as an interop laboratory, not as the target architecture. Its build:

- uses JDK 17 as the build toolchain,
- can target Java 8 class files for legacy Minecraft,
- produces a self-contained injection payload,
- relocates ASM and Javassist under the client namespace to avoid classloader conflicts,
- verifies required classes/packages and rejects Java 9+ bytecode when targeting Java 8,
- pairs the Java payload with a Windows x64 JNI/JVMTI bridge.

Those verification and isolation techniques are worth reproducing where relevant. The native injection architecture itself is unnecessary for the primary client if our launcher owns process creation.

The repository's CC0 notice is deliberately limited to material its contributors actually have rights to, and the README explicitly says the project is recovered research rather than official source. Therefore the project is not treated as a blanket permissive code donor.

### Code-level donor findings added after the structural pass

#### KRS hot-path dispatch and lifecycle

KRS is stronger as an engineering reference than its modern-version mismatch initially suggested.

Its module registry is explicit, but setting fields are discovered once and cached. More importantly, its event manager does only registration-time route discovery and then publishes immutable listener-array snapshots. Hot-path event dispatch therefore becomes indexed array iteration rather than reflective invocation. Module enablement also has rollback behavior if `onEnable()` throws, and shutdown explicitly unregisters/disposes listeners.

Its config manager contributes several useful persistence ideas:

- separate module and bind profiles,
- filename normalization and Windows reserved-name handling,
- maximum config size bounds,
- asynchronous online-profile generation tokens to reject stale responses,
- UTF-8 writes through a temporary file,
- `force(true)` followed by atomic move where supported.

Those ideas are good clean-room candidates. KRS's settings-field reflection and GPL implementation are not.

KRS's NanoVG renderer also uses bounded queues and swaps producer/frame deques instead of copying queued work. That queue ownership model is useful. Its broad `glGet*` state snapshotting, however, conflicts with the late-Drippy goal of avoiding synchronous GL readbacks on hot render paths, so that part is specifically rejected for the 1.8.9 renderer.

#### Rise acquisition, registration and shader staging

Rise's recovered build demonstrates a useful **runtime acquisition** pattern: it can fetch the official Minecraft 1.8.9 version metadata/client/assets, verify expected SHA-1 values, provision natives/assets, and support an existing local Minecraft install.

That is substantially better than committing Mojang assets. However, the recovered project still compiles/packages against a committed `libs/minecraft-deobf.jar` and merges that jar into the client output. The new client must not reproduce that packaging choice.

Rise's runtime architecture separates modules, components, commands, configs, themes, scripts, keybinds, bot state and shader rendering. Its shader manager keeps independent queues by shader type and render domain, drains them at render boundaries, and clears them deterministically afterward. This is the strongest verified reference for staged blur/bloom/overlay composition in the current donor set.

The recovered client also contains reflection-based package discovery paths, so the manager boundaries are useful while reflection scanning is not.

#### OpenOnyx shared services

The recovered Onyx source confirms that rotation, aiming and combat packet/state coordination are separate responsibilities rather than incidental logic hidden inside one module. `KillAura` owns references to `CombatController` and `AimController`; `RotationManager` centralizes the rotation state seen by movement/protocol consumers; typed settings support predicates/visibility and JSON serialization.

The specific recovered implementation is not reusable, but this strongly supports a clean-room design in which rotation, target selection and combat action sequencing are shared services with explicit lifecycle/state snapshots.

#### Raven scripting and profiles

The public Raven bS+ comparator uses explicit module registration and indexes modules by exact name, normalized name and class. Its scripting system compiles scripts into module-like objects and uses a constrained classloader/import model. Profiles can resolve both built-in and script-provided modules and stage desired enabled/keybind/hidden state during load.

That makes Raven the strongest current **scripting sandbox/profile integration** reference. Exact lineage/license parity with the uploaded bS/bS+ archives is still unresolved, so implementation remains reference-only until provenance is proven.

#### FDP verification discipline

FDP B17 contributes more than typed settings. Its build includes deterministic foundation verification tasks, SHA-256 verification for bundled libraries, and a class-file-major-version gate that fails the fat-JAR build if Java 9+ bytecode would make Forge 1.8.9's ASM5 reject the client.

Those are excellent CI concepts for the clean-room project: validate runtime compatibility and artifact provenance before release rather than discovering them at launch time.

#### Yuri provenance correction

Yuri remains a valuable proof that a 1.8.9 client can be launched directly without making Forge the permanent shell. But the source tree itself contains Minecraft classes, including `net.minecraft.client.main.Main`, in addition to the README's acknowledgement of snippets from other clients.

Therefore the repository-level MIT file cannot be treated as permission for the whole tree. Yuri is now a **host-shape/reference donor**, not a generally reusable source donor. The clean-room host must acquire the user's legitimate Minecraft runtime and apply our own integration layer rather than copying Yuri's embedded game source.

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
- **Build-time verification gates** should borrow the idea—not implementation—from Vape recovery: bytecode-level compatibility, dependency-completeness and classloader-boundary checks should be automated in CI.
- **Provenance is a per-file property**, not something inferred solely from a repository's root license.

## Audit blockers / uncertainty

The current chat archive backend is registering all uploaded ZIP/JAR files but is failing to expose ZIP internals for direct static extraction. The model-side container is also currently unavailable for local archive inspection. Therefore:

- no claim is made that a public repo is byte-for-byte identical to an uploaded archive;
- no claim is made about an archive-only donor's exact module list or license unless separately source-backed;
- none of the uploaded binaries have been executed;
- Drippy's fresh audit pack still needs exact static verification before its late runtime state is treated as fresh authority;
- exact uploaded Raven/OpenExpo/Flux/binary-only lineage remains unresolved.

This uncertainty must be resolved before the donor audit is marked complete and before any donor-specific implementation is translated into the new client.
