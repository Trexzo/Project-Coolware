# Donor Audit Coverage Ledger

Status: **source-backed audit substantially complete; archive-only exact parity remains blocked by tooling**

This ledger exists so every distinct uploaded donor family is explicitly accounted for. Duplicate archives/revisions are grouped under one family unless they represent materially different source/runtime lineage.

Depth labels:

- **DEEP** — build/runtime plus core/module/event/config/render or launcher code inspected.
- **STRUCTURAL** — build/runtime/license/source shape inspected, but not all subsystems.
- **COMPARATOR** — a plausible public/connected source lineage was inspected, but exact byte-for-byte equality with the uploaded artifact is not proven.
- **ARCHIVE-BLOCKED** — upload is registered, but the current archive backend/container cannot expose its internals; no binary has been executed.
- **REFERENCE-ONLY** — provenance/license means it will not supply implementation code even if later unpacked.

| Uploaded donor family | Coverage | What has actually been established | Remaining uncertainty |
|---|---|---|---|
| **Drippy-Modern-AUDIT** | ARCHIVE-BLOCKED + prior authority | Late Coolware/Drippy direction is standalone/direct-host and performance-led; prior project evidence covers batching/caching/hot-path work | Fresh ZIP internals/hash-level state cannot currently be inspected |
| **VapeV4.21 latest CLEAN SOURCE** | DEEP / COMPARATOR | Connected recovery repo build, Java/native boundary, bytecode target checks, shaded ASM/Javassist, payload verification, CC0 limitation and recovery disclaimer | Fresh upload is not proven byte-identical to connected repo |
| **OpenMyau-Plus** | DEEP / COMPARATOR | Forge/Loom/Mixin build, explicit managers/modules, reflective event registration, typed properties/config, RSL integration, GPLv3 | Fresh ZIP/JAR parity not hash-proven |
| **Myau+ runtime JAR** | ARCHIVE-BLOCKED | Runtime family and source build target are known | Exact compiled-source parity not statically verified |
| **OpenOnyx** | DEEP / COMPARATOR / REFERENCE-ONLY | Recovered architecture, shared rotation/aim/combat services, typed settings, ModuleManager, ESP/Glow responsibility split | Exact uploaded ZIP parity not proven |
| **deobfuscated Onyx** | ARCHIVE-BLOCKED / REFERENCE-ONLY | Same recovered family is source-backed by public comparator | Exact archive differences remain unknown |
| **Rise 6.9.5** | DEEP / COMPARATOR / REFERENCE-ONLY | Manager architecture, event cache, explicit module registry, staged shader queues, GUI structure, official MC asset acquisition/verification pattern, decompiled/proprietary provenance | Exact uploaded archive parity not proven |
| **Yuri** | DEEP | Direct 1.8.9 launch shape, runtime dependency set, ImGui/LWJGL2, MIT file plus mixed-snippet warning; actual Mojang source present in tree | Any reusable original file would still need file-level provenance review |
| **KRS** | DEEP | Fabric 26.2/Java25 stack, explicit lifecycle, routed listener snapshots, atomic config writes, NanoVG queue swapping, GPLv3 | Modern-version design must be translated to 1.8.9 constraints |
| **FDPClient b17** | DEEP / COMPARATOR | Official b17 build structure, GPL/provenance caveat, deterministic verification tasks, SHA-256 library manifest, Java8 class gate, service-rich startup | Uploaded ZIP exact parity not hash-proven |
| **LibreBounce** | STRUCTURAL | Forge1.8.9 Java/Kotlin Mixin stack, GPLv3, explicit extra-source-rights warning | No reason to deepen for code reuse because provenance/license already limits role to concepts |
| **Raven bS / bS+** | DEEP / COMPARATOR | Public bS+ comparator: explicit module registry, profiles, script-as-module integration, runtime compiler, constrained classloader/import model | Exact uploaded bS/bS+ lineage and license remain unresolved |
| **OpenAbyss** | DEEP | Java8 LaunchWrapper/ASM coremod build, recovered/decrypted source, no top-level license | Fresh ZIP parity not hash-proven; reference-only regardless |
| **OpenExpo** | ARCHIVE-BLOCKED | Upload is present; connected mirror exposes no useful verified root build/license metadata | Exact architecture and license unresolved |
| **Flux** | ARCHIVE-BLOCKED / STRUCTURAL | Upload present; connected mirror has only minimal README and no verified root build/license metadata | Architecture/license unresolved |
| **Breeze** | ARCHIVE-BLOCKED / REFERENCE-ONLY | Crack/binary provenance is known | Exact internals unavailable; only future black-box/static reference |
| **Timewarp** | ARCHIVE-BLOCKED / REFERENCE-ONLY | Crack/binary provenance is known | Exact internals unavailable |
| **Slinky cracked** | ARCHIVE-BLOCKED / REFERENCE-ONLY | Crack-oriented/proprietary lineage | Exact internals unavailable |
| **AUGUSTUS** | ARCHIVE-BLOCKED / REFERENCE-ONLY | Large package present; proprietary/package lineage | Exact internals and version identity unavailable |
| **LeakCryptix** | ARCHIVE-BLOCKED / REFERENCE-ONLY | Recovered/crack source package present | Exact internals/license unavailable |
| **raven-bS-16 JAR** | ARCHIVE-BLOCKED / REFERENCE-ONLY | Family backed by Raven comparator | Exact JAR/source parity unavailable |

## Coverage decision

The unavailable archive internals do **not** block selecting the clean-room core architecture because every major architectural question already has multiple independently source-backed comparators:

- host/runtime: Drippy prior authority + Yuri + Rise acquisition pattern;
- lifecycle/events: KRS + Myau + Rise + legacy Coolware anti-pattern comparison;
- settings/config: FDP + KRS + Myau + Onyx;
- render/UI: Rise + Onyx + KRS + prior Drippy performance constraints;
- scripting: Raven + Myau;
- transformation/classloading: Vape recovery + OpenAbyss.

Archive-only cracked/proprietary donors can still contribute later **behavioral/visual observations**, but they cannot become implementation authorities and therefore should not delay the clean-room foundation indefinitely.

## Exact blockers retained

1. Fresh Drippy audit ZIP cannot currently be unpacked, so its exact latest source state is not re-certified here.
2. Uploaded-vs-public parity is not assumed for Rise, Onyx, Raven, Myau, FDP, or Vape.
3. Flux/OpenExpo and binary-only donor internals remain unresolved.
4. No uploaded executable/JAR/DLL has been run.
5. No donor code is authorized for direct reuse solely by appearing in this ledger.
