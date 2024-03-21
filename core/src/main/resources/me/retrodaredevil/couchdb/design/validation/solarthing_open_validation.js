// This validation function made specifically for solarthing_open

function(newDoc, oldDoc, userCtx, secObj) {
    var is_server_or_database_admin = function(userCtx, secObj) {
        // see if the user is a server admin
        if(userCtx.roles.indexOf('_admin') !== -1) {
            return true; // a server admin
        }

        // see if the user a database admin specified by name
        if(secObj && secObj.admins && secObj.admins.names) {
            if(secObj.admins.names.indexOf(userCtx.name) !== -1) {
                return true; // database admin
            }
        }

        // see if the user a database admin specified by role
        if(secObj && secObj.admins && secObj.admins.roles) {
            var db_roles = secObj.admins.roles;
            for(var idx = 0; idx < userCtx.roles.length; idx++) {
                var user_role = userCtx.roles[idx];
                if(db_roles.indexOf(user_role) !== -1) {
                    return true; // role matches!
                }
            }
        }

        return false; // default to no admin
    }

    // Perform certain checks only if this is a non-admin user
    if (!is_server_or_database_admin(userCtx, secObj)) {
        if (oldDoc) { // If someone is trying to update a document, prevent them from doing that
            throw {'unauthorized':'You are not authorized to change this document!'};
        }
        if (typeof newDoc.dateMillis !== "number") {
            throw {forbidden: "dateMillis must be a number"}
        }
        var currentMillis = +new Date();
        var millisInPast = currentMillis - newDoc.dateMillis;
        // Not only do we not want people to upload data that's far in the past,
        //   we have strict rules that disallow documents targeted for the future.
        if (millisInPast > 25 * 60 * 1000) {
            throw {forbidden: "dateMillis field is too far in the past!"}
        }
        if (-millisInPast > 3 * 60 * 1000) {
            throw {forbidden: "dateMillis field is too far in the future!"}
        }
    }
}
