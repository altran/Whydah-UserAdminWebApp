var myJsonRoleDataStore =  new Ext.data.JsonStore({
//  url: './ext3/test/useridentity.html',
//  url: 'http://localhost/useridentity.json',
//        url: './jsondata',
        url: myHostJsonUsers+search_uid,
        root: 'propsAndRoles',
        autoLoad: true,
        autoSave: false,

        fields: [
            'appId', 'orgID',
            'roleName', 'roleValue', 'applicationName', 'organizationName',
        ]
});


myJsonRoleDataStore.on('load', function () {
    myJsonRoleDataStore.data.each(function(item, index, totalItems ) {
        //Bus.fireEvent('message', item.data ['brukernavn']);
        //Bus.fireEvent('message', item.data ['brukernavn']);
        //alert(item.data ['roleName']);
    });
});
