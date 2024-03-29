// solarthing, solarthing_alter, solarthing_events, solarthing_closed

// noinspection JSUnusedGlobalSymbols,ReservedWordAsName

// Readonly auth is designed to allow databases to be made public to the world, but not allow edits.
// This also makes sure that any document with a dateMillis property is not uploading old data.
//   This will not affect documents without a dateMillis property, so this can be used for many databases.

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

    if(!is_server_or_database_admin(userCtx, secObj)) {
        throw {'unauthorized':'This is read only when unauthorized'};
    }

    // Only check dateMillis if this is a new document and dateMillis is present on the packet
    if (!oldDoc && newDoc.dateMillis !== undefined) {
        var currentMillis = +new Date();
        var millisInPast = currentMillis - newDoc.dateMillis;
        if (millisInPast > 25 * 60 * 1000) {
            throw {forbidden: "dateMillis field is too far in the past!"}
        }
    }
}
