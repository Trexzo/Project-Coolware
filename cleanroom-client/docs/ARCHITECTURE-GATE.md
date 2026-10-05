# Clean-room Architecture Gate

Status: **PASS for core development; platform-host implementation remains gated by runtime acquisition tests**

This decision is based on the donor audit and coverage ledger. It freezes only the foundation choices that are supported by multiple independent references.

## 1. Product shape

The target is a **launcher-owned standalone Minecraft client**, not a permanent Forge mod.

The launcher owns:

- runtime/profile selection,
- official Minecraft version metadata acquisition,
- verification of downloaded client/libraries/assets,
- JVM selection and arguments,
- process creation,
- update/rollback metadata,
- diagnostics and crash handoff.

The client owns:

- clean-room core lifecycle,
- modules/services/settings/configuration,
- platform bridge contracts,
- render queues/snapshots,
- features and UI.

Forge may remain useful as a temporary development comparator, but it is **not** the production shell.

## 2. Minecraft 1.8.9 integration boundary

No Mojang/Minecraft source or client binary is committed as project source.

The intended production path is:

1. acquire the user's official 1.8.9 runtime from Mojang metadata or a verified existing install;
2. verify expected hashes/metadata before launch;
3. build the runtime classpath without repackaging the Minecraft client into our repository;
4. install clean-room hooks through a dedicated platform layer;
5. keep all obfuscated/version-specific names outside the core.

The preferred first implementation candidate is a launcher-supplied Java instrumentation/transform layer rather than Forge or JNI/JVMTI injection. That choice is **not yet frozen** until a minimal legal/reproducible 1.8.9 bootstrap is proven in CI/local acceptance.

The launcher/core can remain Java 21. Compatibility-facing platform classes should initially target Java 8 bytecode where useful; Java 21 can execute that bytecode while we prove old-library compatibility.

## 3. Core lifecycle

Use explicit construction and ownership:

- `ClientCore` owns long-lived services.
- `ModuleRegistry` owns modules by stable logical ID.
- `SettingRegistry` owns settings by stable qualified ID.
- feature modules receive required services explicitly.
- startup and shutdown order are deterministic.

Rejected:

- classpath/package reflection scans,
- generated event-bus Java source,
- global static discovery as the primary dependency mechanism.

## 4. Events

The current clean-room `EventBus` is acceptable for M1:

- no reflection,
- exact typed event keys,
- no allocation required for normal dispatch beyond the event itself,
- subscription mutation is rare, so copy-on-write buckets are acceptable.

Future hot platform routes may use immutable listener-array snapshots when measurement proves it useful. Do not introduce reflection merely to imitate donor APIs.

## 5. Modules

A module has:

- stable ID,
- display metadata,
- category,
- deterministic enable/disable lifecycle.

Enable failure must restore the previous state.

Module toggling does **not** synchronously write the entire config. Persistence is an independent service.

## 6. Settings

Settings require:

- stable IDs independent of labels,
- typed values,
- validation,
- optional visibility rules,
- stable aliases for renamed settings,
- explicit codecs for persistence,
- no reflected setting fields.

Display names/descriptions belong to UI metadata and must not be persistence keys.

## 7. Profiles and persistence

Profiles store:

- module enabled state,
- module keybind/visibility state when those land,
- setting values by stable qualified ID,
- schema version.

Requirements:

- deterministic serialization,
- parse/validate before mutation,
- alias migration,
- bounded input size,
- rollback/transaction semantics for failed application,
- temporary-file write + flush + atomic move where supported,
- debounced/explicit save rather than save-on-every-setter.

## 8. Render architecture

Render work is staged:

- world/3D,
- HUD/2D,
- blur masks,
- bloom/glow masks,
- overlays/final composition.

Requirements:

- bounded queues,
- producer/frame queue swap instead of copying,
- clear queues deterministically after consumption/failure,
- immutable frame/scene snapshots shared by ESP/HUD consumers,
- cached text/layout where measurement supports it,
- no broad `glGet*` state snapshots on hot paths,
- explicit render-state ownership and minimal transitions.

Native Java/LWJGL rendering is the default. A browser engine is not a required ClickGUI dependency.

## 9. Shared gameplay services

Cross-module behavior belongs in services rather than being reimplemented inside modules.

Planned service boundaries include:

- rotation,
- target selection/relations,
- combat action sequencing,
- packet scheduling/buffering,
- world/entity snapshot,
- input/keybind state.

The service contracts are clean-room designs; recovered Onyx/Rise implementations are behavioral references only.

## 10. Extensibility

Scripting/plugins are post-foundation.

The API should expose stable logical client abstractions rather than obfuscated Minecraft classes.

Raven's script-as-module/profile integration and Myau's loose scripting bridge are useful references, but arbitrary unrestricted runtime Java compilation is not accepted as the default security model.

## 11. CI / provenance gates

CI must eventually prove:

- unit tests,
- deterministic core/profile behavior,
- platform bytecode compatibility,
- no unexpected Java-major versions in legacy-facing artifacts,
- dependency/artifact checksum manifests where binaries are bundled,
- no Mojang source/client JAR committed under project source,
- no forbidden donor package/code import,
- reproducible launcher/runtime metadata.

## 12. Donor ideas selected

**Adapt independently**

- Drippy: measured hot-path discipline, batching/caching, standalone ownership.
- Yuri: direct-launch shape only.
- Rise: manager/render-stage boundaries and official runtime acquisition verification.
- Onyx: shared rotation/aim/combat responsibility boundaries.
- KRS: listener snapshots, lifecycle rollback, atomic config writes, bounded render queues.
- FDP: typed configuration ideas and build verification gates.
- Raven: script/profile/module integration concepts.
- Myau+: explicit manager/module construction and loose scripting boundary.
- Vape recovery: classloader/dependency/bytecode verification concepts.

**Reject as foundation**

- legacy Coolware reflection/generated event bus/save-on-toggle,
- Forge as permanent shell,
- browser ClickGUI as mandatory UI,
- JNI/JVMTI injection when launcher owns the process,
- full GL state readback on every render batch,
- recovered/proprietary implementations,
- GPL implementation in a permissive core,
- embedded Mojang source or repackaged client source.

## 13. Next development sequence

M1:
1. setting registry + codecs + aliases;
2. deterministic profile snapshot/codec;
3. transactional apply;
4. atomic profile store;
5. lifecycle/client-core composition.

M2:
1. launcher profile model;
2. official Mojang metadata/client/library acquisition;
3. SHA verification and cache;
4. JVM/runtime selection;
5. process command construction with dry-run tests.

M3:
1. 1.8.9 platform API;
2. minimal instrumentation/transform proof;
3. one tick + one render hook;
4. local acceptance against a legitimate client install.

Only after M3 is proven do feature/render-module lanes accelerate.
