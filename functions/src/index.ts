import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
admin.initializeApp();

exports.addItem = functions.https.onRequest(async (request: functions.https.Request, response: functions.Response) => {
    if (request.method !== 'POST') {
        response.status(405).send('Method Not Allowed');
        return;
    }
    try {
        const data = request.body;
        const firestore = admin.firestore();
        const docRef = await firestore.collection('items').add(data);
        response.status(201).send(`Created a new item: ${docRef.id}`);
    } catch (error) {
        const message = (error as Error).message;
        response.status(500).send(message);
    }
});