  Bus = new Ext.util.Observable();
  var myJsonIdentityStore =  new Ext.data.JsonStore({
//  url: './ext3/test/useridentity.html',
   url: myHostJsonUsers+search_uid,
//        url: './json',
//        url: './jsondata',
        root: 'identity',
        autoLoad: true,
        autoSave: false,
        id : 'username',

        fields: [
            'personRef', 'uid', 'username',
            'firstName', 'lastName', 'email', 'cellPhone'
        ]

});

 function renderUrl(value, p, record){
        return String.format('<strong><a href="{1}" title="{3}">{2}</a></strong>', value, record.data.url_path, record.id, record.data.name);
 }


myJsonIdentityStore.on('datachanged', function () {  //'load'
    //alert(item.data ['brukernavn']);
    myJsonIdentityStore.data.each(function(item, index, totalItems ) {
       // Bus.fireEvent('message', item.data ['brukernavn']);
        Bus.fireEvent('message', item.data ['username']);

    });
//                 brukernavn_field.setValue(myJsonIdentityStore.data.items[0].get('uid'));
//                 firstName_field.setValue(myJsonIdentityStore.data.items[0].get('firstName'));
//                 lastname_field.setValue(myJsonIdentityStore.data.items[0].get('lastName'));
//                 email_field.setValue(myJsonIdentityStore.data.items[0].get('email'));
//                 cell_field.setValue(myJsonIdentityStore.data.items[0].get('cell'));

});




//myJsonIdentityStore.on('message', function () {
//  myJsonIdentityStore.reload();
//});