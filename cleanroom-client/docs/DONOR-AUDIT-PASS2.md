# Donor Audit — Pass 2 Evidence

Status: verified source-backed follow-up  
Branch: `incubator/cleanroom-client`

This file records only observations verified in the connected source repositories during the second audit pass. It does not authorize copying donor implementation.

## Yuri

Repository: `Trexzo/Yuri`

Verified:

- Repository-level license: MIT.
- Gradle application main class: `net.minecraft.client.main.Main`.
- The project declares the legacy Minecraft 1.8.9 runtime libraries directly, including LWJGL 2, Netty, Authlib, Guava, Gson, JInput and the legacy sound stack.
- README describes packaged releases with a bundled Java 8 runtime and OS launch scripts.
- ShadowJar is part of the build.

Architecture consequence:

Yuri is the strongest currently source-verifiable example of a lightweight direct-launch 1.8.9 distribution. The target should independently reproduce the launcher/process ownership concept, but repository-level MIT status is not sufficient to treat bundled Mojang/Minecraft source or third-party snippets as permissively reusable.

## KRS

Repository: `Trexzo/Krs`

Verified:

- GPLv3.
- Modern Fabric/Loom project.
- Uses split environment source sets and an access widener.
- Uses ImGui Java bindings plus NanoVG/STB/LWJGL 3-era rendering dependencies.
- README identifies the current family as KRS 26.2.

Architecture consequence:

KRS is a modern rendering/UI comparison donor, not a 1.8.9 host donor. Its value is in contemporary UI composition, rendering abstraction and separation of client-only sources. Direct code reuse would carry GPL obligations.

## LibreBounce

Repository: `Trexzo/LibreBounce`

Verified:

- GPLv3.
- Forge 1.8.9.
- Java 8 target plus Kotlin.
- Mixin/coremod injection model.
- LiquidBounce Legacy lineage.
- README explicitly warns that additional development/build source may exist to which the project claims no rights.
- Dependency surface includes Mixin, Kotlin/coroutines, Discord IPC, auth helpers, charting, HTTP, semver and Swing theming.

Architecture consequence:

LibreBounce is useful as a mature legacy framework comparison, but it is too dependency-heavy and provenance-complicated to become the new foundation. The clean-room project should copy neither its source nor its dependency shape.

## OpenAbyss

Repository: `Trexzo/OpenAbyss`

Verified:

- Described as deobfuscated/decrypted Forge 1.8.9 source.
- Java 8 target.
- LaunchWrapper + ASM coremod architecture.
- Manifest declares `Abyss.ASM.CoreMod`, `FMLCorePluginContainsFMLMod`, and `ForceLoadAsMod`.
- Build still contains hard-coded compile-only paths to a local Minecraft installation.
- No top-level license was established in this pass.

Architecture consequence:

OpenAbyss is a compatibility/recovery reference only. Hard-coded local library roots, LaunchWrapper ownership and recovered-source provenance are all rejected as foundation choices.

## Vape 4.21 recovery

Repository: `Trexzo/VapeV4.21`

Verified:

- README explicitly describes the repository as a research recovery project, not official Vape source.
- Java layer plus Windows x64 JNI/JVMTI bridge.
- Build toolchain uses JDK 17 while supporting `targetRelease=8` for 1.8.9 compatibility.
- Build includes explicit recovered-source quality checks, injection payload generation, payload verification and native bundle preparation.
- Native payload strategy embeds the injection JAR in the DLL and loads it into a target JVM.
- Repository applies CC0 only to material contributors have the right to dedicate; third-party/recovered material remains caveated.
- Java build uses ASM and Javassist.

Architecture consequence:

The injection mechanism itself is unnecessary for a launcher-owned client, but its verification discipline is valuable. We should independently adopt payload-content checks, bytecode-version checks, dependency-boundary checks and deterministic packaging gates.

## Legacy Project-Coolware

Repository: `Trexzo/Project-Coolware`

Verified:

- Forge 1.8.9, Java 8, ForgeGradle 2.1 and Mixin 0.7.11.
- Reflection dependency is used for module discovery.
- The build invokes `createEventBus()` during Gradle configuration and generates/uses a custom event-bus path.
- Gradle configuration allocates up to 8 GB largely to support the legacy decompile/build workflow.
- Main README identifies the existing startup/module/command/mixin structure as a Forge mod.

Architecture consequence:

Legacy Coolware remains useful for behavior and UX lineage, but the new client should not inherit its reflection scan, generated-event-bus build coupling, Forge shell or high-overhead decompile-oriented project structure.

## Revised architectural direction

The source-backed evidence now supports this target split:

1. **Launcher/runtime ownership:** Drippy prior authority + independently reimplemented Yuri-style direct-launch shape.
2. **Platform bridge:** explicit version-specific boundary; no permanent Forge shell requirement.
3. **Core:** explicit registries and lifecycle-owned services; no reflection discovery.
4. **Events:** typed in-process dispatch with deterministic subscription ownership; no generated event-bus source.
5. **Settings/config:** typed values and atomic profile persistence, inspired by mature clients but independently implemented.
6. **Rendering:** staged render graph with cached immutable frame data; legacy LWJGL2 compatibility isolated behind render interfaces.
7. **UI:** native in-process UI stack; modern ideas from KRS/Rise can inform composition, but no mandatory embedded browser.
8. **Verification:** CI must test bytecode target, package contents, dependency closure, deterministic config round trips and launcher/platform contract.
9. **Provenance:** every donor-derived idea gets a design note; recovered/cracked/GPL implementation is not copied into a permissively licensed core.

## Remaining audit blockers

- Fresh `Drippy-Modern-AUDIT.zip` still needs archive-level static inspection when the file backend exposes it.
- Exact uploaded Raven lineage/license still needs verification.
- OpenExpo/Flux and archive-only donors still need unique-value triage.
- Rise/OpenOnyx remain high-value design references but recovered/proprietary implementation stays reference-only.
