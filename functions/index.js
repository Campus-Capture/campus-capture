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



const sections = ["AR", "CGC", "GC", "GM", "EL", "IN", "SV", "MA", "MT", "PH", "MX", "SIE", "SC"]


exports.testScheduledFunction = functions.region('europe-west1').pubsub.schedule("15 * * * *").onRun((context) => {

    console.log("It's minute 15!");
  
    const db = getDatabase();
    const ref = db.ref('Zones');


    ref.once('value').then( (snapshot) => {

        console.log("snapshot key " + snapshot.key)
        snapshot.forEach((childSnapshot) => {

            var zone_name = childSnapshot.key;
            console.log("childSnapshot key " + zone_name.key)

            var owner = "no owner"
            var max = 0

            for(var i = 0, size = sections.length; i < size ; i++){

                var section = sections[i]

                if(childSnapshot.hasChild(section)){

                    let val_section = childSnapshot.child(section).val()

                    console.log(section + " " + val_section)
                    if(val_section > max){
                        console.log("val > max")
                        max = val_section
                        owner = section
                    }
                }

            }

            console.log("new owner is of zone " + zone_name + " is " + owner)

            ref.child(zone_name).child("owner").set(owner, (error) => {
                if (error) {
                  console.log('Data could not be saved.' + error);
                } else {
                  console.log('Data saved successfully.');
                }
            });
        })
    });

    return null;
});
