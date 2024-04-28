const admin = require('firebase-admin');
const serviceAccount = require('../src/main/resources/firebase_config.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

const uploadData = async (collectionName, data) => {
  const collectionRef = db.collection(collectionName);
  const batch = db.batch();

  data.forEach((doc) => {
    const docRef = collectionRef.doc(); // Automatically generate unique ID
    batch.set(docRef, doc);
  });

  await batch.commit();
  console.log(`Successfully uploaded data to the ${collectionName} collection.`);
};

const users = require('../data/users.json');
const items = require('../data/items.json');
const interactions = require('../data/interactions.json');

uploadData('users', users);
uploadData('items', items);
uploadData('interactions', interactions);