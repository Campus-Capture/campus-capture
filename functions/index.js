const functions = require("firebase-functions");

// Import Admin SDK
const { getDatabase } = require('firebase-admin/database');

const admin = require('firebase-admin');
const { user } = require("firebase-functions/v1/auth");
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

function set_error_callback (error) {
    if (error) {
      console.log('Data could not be saved.' + error)
    } else {
      console.log('Data saved successfully.')
    }
}

exports.resetVotesScheduledFunction = functions.region('europe-west1').pubsub.schedule("0 * * * *").onRun((context) => {

    console.log("It's minute 0!");
  
    const db = getDatabase();
    const refZones = db.ref('Zones');
    const refUsers = db.ref('Users');


    refZones.once('value').then( (zonesSnapshot) => {

        // reset for each zone
        zonesSnapshot.forEach((zoneSnapshot) => {

            var zone_name = zoneSnapshot.key

            zoneSnapshot.forEach((zoneChildSnapshot) => {
                
                if (zoneChildSnapshot.key != "owner"){

                    var section_name = zoneChildSnapshot.key

                    // reset count to 0
                    refZones.child(zone_name).child(section_name).set(0, set_error_callback)
                }
            })
        })
    })

    refUsers.once('value').then( (usersSnapshot) => {

        // reset for each user
        usersSnapshot.forEach((userSnapshot) => {

            var user_id = userSnapshot.key;

            refUsers.child(user_id).child("has_voted").set(false, set_error_callback)
        })
    })

    return null
})

exports.countVotesScheduledFunction = functions.region('europe-west1').pubsub.schedule("15 * * * *").onRun((context) => {

    console.log("It's minute 15!")
  
    const db = getDatabase()
    const refZones = db.ref('Zones')
    const refSections = db.ref('Sections')

    const sectionsScores = new Map()
    for(var i = 0, size = sections.length; i < size ; i++){
        sectionsScores.set(sections[i], 0);
    }
    sectionsScores.set("no owner", 0);

    refZones.once('value').then( (zonesSnapshot) => {

        // update for each zone
        zonesSnapshot.forEach((zoneChildSnapshot) => {

            var zone_name = zoneChildSnapshot.key

            var new_owner = "no owner"
            var current_owner = zoneChildSnapshot.child("owner").val()

            var max = 0
            var second_max = 0

            // for each section
            for(var i = 0, size = sections.length; i < size ; i++){

                var section = sections[i]

                if(zoneChildSnapshot.hasChild(section)){

                    let val_section = zoneChildSnapshot.child(section).val()

                    if(val_section > max){
                        max = val_section
                        new_owner = section
                    } else if(val_section > second_max){
                        second_max = val_section
                    }
                }
            }
        

            // no capture if equality
            if(max != second_max){

                sectionsScores.set(new_owner, sectionsScores.get(new_owner) + 1)
    
                refZones.child(zone_name).child("owner").set(new_owner, set_error_callback)
            } else {

                sectionsScores.set(current_owner, sectionsScores.get(current_owner) + 1)

            }

        })

        sectionsScores.forEach( (val, key) => {
            if(key != "no owner"){
                refSections.child(key).child("score").set(val, set_error_callback)
            }
        })

    })

    return null
})
