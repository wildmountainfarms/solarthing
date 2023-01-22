// noinspection JSUnusedGlobalSymbols,ReservedWordAsName

function(newDoc, oldDoc, userCtx, secObj) {

    secObj.admins = secObj.admins || {};
    secObj.admins.names = secObj.admins.names || [];
    secObj.admins.roles = secObj.admins.roles || [];

    var isAdmin = false;
    if(userCtx.roles.indexOf('_admin') !== -1) {
        isAdmin = true;
    }
    if(secObj.admins.names.indexOf(userCtx.name) !== -1) {
        isAdmin = true;
    }
    for(var i = 0; i < userCtx.roles.length; i++) {
        if(secObj.admins.roles.indexOf(userCtx.roles[i]) !== -1) {
            isAdmin = true;
        }
    }

    if(!isAdmin) {
        throw {'unauthorized':'This is read only when unauthorized'};
    }
}
