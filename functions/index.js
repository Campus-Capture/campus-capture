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

exports.resetVotesScheduledFunction = functions.region('europe-west1').pubsub.schedule("* * * * *").onRun((context) => {

    console.log("It's minute 0!");
  
    const db = getDatabase();
    const refZones = db.ref('Zones');
    const refUsers = db.ref('Users');


    refZones.once('value').then( (zonesSnapshot) => {

        // reset for each zone
        zonesSnapshot.forEach((zoneSnapshot) => {
            zoneSnapshot.forEach((zoneChildSnapshot) => {
                if (zoneChildSnapshot.key != "owner"){
                    // reset count to 0
                    zoneChildSnapshot.set(0, (error) => {
                        if (error) {
                          console.log('Data could not be saved.' + error)
                        } else {
                          console.log('Data saved successfully.')
                        }
                    })
                }
            })
        })
    })

    refUsers.once('value').then( (usersSnapshot) => {

        // reset for each user
        usersSnapshot.forEach((userSnapshot) => {
            userSnapshot.child("has_voted").set(false, (error) => {
                if (error) {
                  console.log('Data could not be saved.' + error)
                } else {
                  console.log('Data saved successfully.')
                }
            })
        })
    })

    return null
})

exports.countVotesScheduledFunction = functions.region('europe-west1').pubsub.schedule("15 * * * *").onRun((context) => {

    console.log("It's minute 15!");
  
    const db = getDatabase();
    const refZones = db.ref('Zones');

    refZones.once('value').then( (zonesSnapshot) => {

        // update for each zone
        zonesSnapshot.forEach((zoneChildSnapshot) => {

            var zone_name = zoneChildSnapshot.key;

            var owner = "no owner"
            var max = 0
            var second_max = 0

            // for each section
            for(var i = 0, size = sections.length; i < size ; i++){

                var section = sections[i]

                if(zoneChildSnapshot.hasChild(section)){

                    let val_section = zoneChildSnapshot.child(section).val()

                    console.log(section + " " + val_section)
                    if(val_section > max){
                        max = val_section
                        owner = section
                    } else if(val_section > second_max){
                        second_max = val_section
                    }
                }
            }

            // no capture if equality
            if(max != second_max){
    
                ref.child(zone_name).child("owner").set(owner, (error) => {
                    if (error) {
                      console.log('Data could not be saved.' + error);
                    } else {
                      console.log('Data saved successfully.');
                    }
                });
            } else {
                console.log("equality, no new owner for zone " + zone_name)

            }

        })
    });

    return null;
});
