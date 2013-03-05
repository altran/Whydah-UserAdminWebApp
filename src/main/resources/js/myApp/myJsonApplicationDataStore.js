var myJsonApplicationDataStore =  new Ext.data.JsonStore({
//  url: './ext3/test/useridentity.html',
//  url: 'http://localhost/useridentity.json',
//        url: './jsondata',
        url: myHostJsonUsers+search_uid+'/applications',
        root: 'applications',
        autoLoad: true,
//        autoSave: false,

        fields: [
            'appId', 'applicationName', 'hasRoles',
        ]
});


myJsonApplicationDataStore.on('load', function () {
    myJsonApplicationDataStore.data.each(function(item, index, totalItems ) {
        //Bus.fireEvent('message', item.data ['brukernavn']);
        //Bus.fireEvent('message', item.data ['brukernavn']);
        //alert(item.data ['roleName']);
    });
});

myJsonApplicationDataStore.on('datachanged', function () {
    myJsonApplicationDataStore.data.each(function(item, index, totalItems ) {
        //Bus.fireEvent('message', item.data ['brukernavn']);
        //Bus.fireEvent('message', item.data ['brukernavn']);
  //      alert('update'+item.data ['hasRoles']);
    });
});
