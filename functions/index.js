const functions = require("firebase-functions");

// // Create and deploy your first functions
// // https://firebase.google.com/docs/functions/get-started
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//    functions.logger.info("Hello logs!", {structuredData: true});
//    response.send("Hello from Firebase!");
//  });

// the schedule is specified in cron job format "15 * * * *" = "At minute 15."
exports.scheduledFunction = functions.region('europe-west1').pubsub.schedule("15 * * * *").onRun((context) => {
  
  //functions.logger.info("It's minute 15!");
  console.log("It's minute 15!");


  return null;
});
