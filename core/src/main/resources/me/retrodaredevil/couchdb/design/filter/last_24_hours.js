// noinspection JSUnusedGlobalSymbols,ReservedWordAsName,ES6ConvertVarToLetConst

function(doc, req) {
  var durationMillis = 1000 * 60 * 60 * 24;
  var currentMillis = +new Date();
  var documentDateMillis = doc.dateMillis;
  return currentMillis - durationMillis <= documentDateMillis; // make sure documentDateMillis is after some point
}
