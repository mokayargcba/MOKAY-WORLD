const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_mokay_01";
const BOB_UID = "bob_mokay_02";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validSavePayload(uid, nowMs = Date.now()) {
  const ts = new Date(nowMs - 2000);
  return {
    userId: uid,
    currentRegionId: "plaza_infinita",
    playerX: 900,
    playerY: 740,
    hp: 100,
    maxHp: 100,
    stamina: 100,
    maxStamina: 100,
    mana: 80,
    maxMana: 100,
    chispas: 60,
    inventory: {
      slice_pizza: 2,
      cafe_bw: 2,
      elixir_chispa: 1,
    },
    quickSlots: ["slice_pizza", "cafe_bw", "elixir_chispa"],
    collectedBookIds: ["libro_origen"],
    discoveredKeywords: ["CHISPA"],
    discoveredRegions: ["plaza_infinita"],
    discoveredLandmarkIds: ["plaza_center"],
    visitedVillages: ["Mirador Sur"],
    activeQuestIds: ["q_tintopolis_cat"],
    completedQuests: [],
    trackedQuestId: "q_tintopolis_cat",
    solvedPuzzleIds: [],
    defeatedBossStages: [],
    unlockedGateIds: [],
    collectedForces: [],
    collectedPickupIds: ["pp_book"],
    npcRelationships: {
      npc_llave: 15,
    },
    unlockedAchievementIds: ["ach_explorer_3"],
    createdAt: ts,
    updatedAt: ts,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write Mokay World saves", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("saves").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("saves")
      .doc("main")
      .set(validSavePayload(ALICE_UID))
  );
});

test("Authenticated owner can create, read, list, and partially update their save", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const saveRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("saves")
    .doc("main");

  await assertSucceeds(saveRef.set(validSavePayload(ALICE_UID)));
  await assertSucceeds(saveRef.get());
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("saves").get()
  );

  await assertSucceeds(
    saveRef.update({
      currentRegionId: "tintopolis",
      playerX: 640.5,
      playerY: 420.25,
      hp: 85,
      maxHp: 115,
      stamina: 95,
      maxStamina: 115,
      mana: 60,
      maxMana: 100,
      chispas: 185,
      inventory: {
        slice_pizza: 1,
        cafe_bw: 2,
        elixir_chispa: 1,
        tinta_antigua: 3,
      },
      discoveredRegions: ["plaza_infinita", "tintopolis"],
      discoveredLandmarkIds: ["plaza_center", "tm_fuente"],
      visitedVillages: ["Mirador Sur", "Barrio Café B&W"],
      activeQuestIds: ["q_tinto_press"],
      completedQuests: ["q_tintopolis_cat"],
      trackedQuestId: "q_tinto_press",
      defeatedBossStages: ["boss_1_cat"],
      unlockedGateIds: ["gate_plaza_tinto"],
      collectedPickupIds: ["pp_book", "tp_ink1"],
      npcRelationships: {
        npc_llave: 35,
        npc_star: 10,
      },
      unlockedAchievementIds: ["ach_explorer_3", "ach_boss_first"],
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Rejects ghost fields, @DocumentId id field, immutable createdAt edits, out-of-range stats, and future timestamps", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const savesCol = aliceDb.collection("users").doc(ALICE_UID).collection("saves");

  // Reject ghost field on create
  const ghostPayload = validSavePayload(ALICE_UID);
  ghostPayload.isAdmin = true;
  await assertFails(savesCol.doc("ghost_save").set(ghostPayload));

  // Reject explicit id field on create (must remain @DocumentId only)
  const withIdPayload = validSavePayload(ALICE_UID);
  withIdPayload.id = "main";
  await assertFails(savesCol.doc("with_id").set(withIdPayload));

  // Reject out-of-range chispas/maxHp
  const badStatsPayload = validSavePayload(ALICE_UID);
  badStatsPayload.chispas = -10;
  await assertFails(savesCol.doc("bad_stats").set(badStatsPayload));

  // Reject future timestamp
  const futurePayload = validSavePayload(ALICE_UID);
  futurePayload.updatedAt = new Date(Date.now() + 60000);
  await assertFails(savesCol.doc("future_save").set(futurePayload));

  // Reject non-map npcRelationships
  const badRelPayload = validSavePayload(ALICE_UID);
  badRelPayload.npcRelationships = "invalid";
  await assertFails(savesCol.doc("bad_rel").set(badRelPayload));

  // Reject non-list unlockedAchievementIds
  const badAchPayload = validSavePayload(ALICE_UID);
  badAchPayload.unlockedAchievementIds = { invalid: true };
  await assertFails(savesCol.doc("bad_ach").set(badAchPayload));

  // Create valid save, then attempt immutable createdAt update
  await assertSucceeds(savesCol.doc("main").set(validSavePayload(ALICE_UID)));
  await assertFails(
    savesCol.doc("main").update({
      createdAt: new Date(Date.now() - 100),
      updatedAt: new Date(Date.now() - 100),
    })
  );
});

test("Cross-user access is strictly denied", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx
      .firestore()
      .collection("users")
      .doc(BOB_UID)
      .collection("saves")
      .doc("main")
      .set(validSavePayload(BOB_UID));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("saves").doc("main").get()
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("saves")
      .doc("main")
      .update({ hp: 10, updatedAt: new Date(Date.now() - 500) })
  );
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("saves").get()
  );
});
