const functions = require("firebase-functions");

// Import Admin SDK
const { getDatabase } = require('firebase-admin/database');

const admin = require('firebase-admin');
admin.initializeApp();

// // Create and deploy your first functions
// // https://firebase.google.com/docs/functions/get-started
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//    functions.logger.info("Hello logs!", {structuredData: true});
//    response.send("Hello from Firebase!");
//  });

// the schedule is specified in cron job format "15 * * * *" = "At minute 15."



const NUMBER_OF_ZONES = 2


exports.testScheduledFunction = functions.region('europe-west1').pubsub.schedule("* * * * *").onRun((context) => {

    console.log("It's minute 15!");
  
    const db = getDatabase();
    const ref = db.ref('Zones');

    //snap.numChildren()


    ref.once('value', (snapshot) => {

        for( let i = 1; i <= NUMBER_OF_ZONES; i++){

            var owner = "no owner"
            var max = 0

            let val_IN = snapshot.child(i).child("IN").val()
            console.log("IN : " + val_IN)
            if(val_IN > max){
                console.log("val > max")
                max = val_IN
                owner = "IN"
            }

            let val_SC = snapshot.child(i).child("SC").val()
            console.log("SC : " + val_SC)
            if(val_SC > max){
                console.log("val > max")
                max = val_SC
                owner = "SC"
            }

            console.log("new owner is " + owner)

            ref.child(i).child("owner").set(owner, (error) => {
                if (error) {
                  console.log('Data could not be saved.' + error);
                } else {
                  console.log('Data saved successfully.');
                }
            });
        }

    });

    return null;
});
