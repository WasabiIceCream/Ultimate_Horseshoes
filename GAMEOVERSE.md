# Gameoverse notes

Fork of Ultimate Horseshoes (`github.com/rokoko1919a-cmd/Ultimate_Horseshoes`, LGPL-3.0),
branch `gameoverse-26.1.2` on upstream's `26.1` branch. `origin` fetches upstream; its push
URL is disabled. Not published (LGPL allows it; just not asked). Added 2026-09-26 to make horse
travel worth investing in: craftable, enchantable horseshoes.

## What we change

- **Crafting**: upstream has none (chest loot and Armorer trades only; no villagers trade on
  this server and its loot goes into vanilla tables most of our structures replace). Shaped
  arch (` X `/`X X`/`X X`), one per tier: Copper Ingot, Iron Sheet (matches the Iron Forge
  Gate, which puts Iron Boots and the Saddle on Iron Sheets), Gold Ingot, Diamond; Netherite
  stays upstream's smithing upgrade. The U shape (`X X`/`X X`/` X `) is taken by buckets
  (vanilla, More Buckets) and `X X`/`X X` is Boots; the arch collided with nothing
  (checked every jar and datapack). Recipe-book unlock on picking up the material.
- **Enchantable**: upstream tags horseshoes as foot armour and horse armour as chest armour but
  gives neither the `enchantable` component, so no enchanting table (Penchant's rework
  included) accepts them, and Penchant's `noAnvilBooks` blocks books. Horseshoes and horse
  armour get their armour material's vanilla enchantability (Copper 8, Iron 9, Gold 25,
  Diamond 10, Netherite 15, Leather 15).
- **Breaking**: horseshoes were damaged with `setDamageValue` and deleted at 0, and upstream
  rolled its own Unbreaking twice (longer wear distance + a skip chance). Now damage goes
  through `hurtAndBreak`, so Unbreaking behaves as Penchant tunes it and Simple Smithing
  Overhaul (`itemDestructionPrevention = ALL`) keeps the item as Broken; a broken horseshoe
  counts as absent (no speed/jump/armour bonus, not copied to the FEET slot, so no enchantment
  effects) until it's sheared off and repaired.
- **Horse armour** (upstream's durability, kept on at the user's request since SSO's Broken
  state covers it through `hurtAndBreak` and `forEachModifier`): also gets a `repairable`
  material, without which broken horse armour could never be repaired.

Config left at upstream defaults (`vanilla_armor_durability` and `slot_lock` on: Shears to
remove horseshoes, the user's choice).

Version `26.1-gameoverse.1`. Build: `./gradlew build`.

## Routine checks

`git fetch origin` and compare `origin/26.1`. Horseman also changes the horse inventory menu
and screen; re-test the horseshoe slot after either mod updates.
