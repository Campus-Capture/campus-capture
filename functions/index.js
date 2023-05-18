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



const sections = ["AR", "CGC", "GC", "GM", "EL", "IN", "SV", "MA", "MT", "PH", "MX", "SIE", "SC", "NONE"]

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

    refZones.once('value').then( (zonesSnapshot) => {

        // update for each zone
        zonesSnapshot.forEach((zoneChildSnapshot) => {

            var zone_name = zoneChildSnapshot.key

            var new_owner = "NONE"
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
            if(key != "NONE"){
                refSections.child(key).child("score").set(val, set_error_callback)
            }
        })

    })

    return null
})

// TODO change to 0 12 * * * once testing done
exports.giveMoneyScheduledFunction = functions.region('europe-west1').pubsub.schedule("* * * * *").onRun((context) => {

    console.log("It's minute noon!")
  
    const db = getDatabase()
    const refUsers = db.ref('Users')

    //take snapshot of Users
    refUsers.once('value').then( (usersSnapshot) => {

        //count number of user for each section
        const sectionsUserCount = new Map()
        for(var i = 0, size = sections.length; i < size ; i++){
            sectionsUserCount.set(sections[i], 0);
        }

        usersSnapshot.forEach((userSnapshot) => {

            let userSection = userSnapshot.child("section").val()
            sectionsUserCount[userSection] = sectionsUserCount[userSection] + 1

        })

        //take snapshot of Sections (for scores)
        //compute how much money each user of each section gets
        const refSections = db.ref('Sections')

        const moneyPerUserPerSection = new Map()
        for(var i = 0, size = sections.length; i < size ; i++){
            moneyPerUserPerSection.set(sections[i], 0);
        }

        refSections.once('value').then( (sectionsSnapshot) => {

            sectionsSnapshot.forEach((sectionSnapshot) => {

                var sectionName = sectionSnapshot.key
    
                let sectionScore = sectionSnapshot.child("score").val()
                moneyPerUserPerSection[sectionName] = floor(sectionScore / sectionsUserCount[sectionName]) + 1
    
            })

        })

        //increment user money
        usersSnapshot.forEach((userSnapshot) => {

            var user_id = userSnapshot.key;

            let userSection = userSnapshot.child("section").val()

            var moneyToAdd = moneyPerUserPerSection[userSection]

            refUsers.child(user_id).child("money").set(admin.database.ServerValue.increment(moneyToAdd), set_error_callback)

        })

    })

    return null;
})
