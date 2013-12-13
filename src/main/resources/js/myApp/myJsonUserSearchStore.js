

var myJsonUserSearchStore =  new Ext.data.JsonStore({
//  url: './ext3/test/useridentity.html',
   url: myHostJsonUserFind+'*',
//        url: './json',
//        url: './jsondata',
        root: 'result',
        autoLoad: true,
        autoSave: false,
        id : 'uid',

        fields: [
            'personRef', 'uid', 'username',
            'firstName', 'lastName', 'email', 'cellPhone'
        ]

});

 function renderUrl(value, p, record){
        return String.format('<strong><a href="{1}" title="{3}">{2}</a></strong>', value, record.data.url_path, record.id, record.data.name);
 }


myJsonUserSearchStore.on('load', function () {
    myJsonUserSearchStore.data.each(function(item, index, totalItems ) {
       // Bus.fireEvent('message', item.data ['brukernavn']);
  //      Bus.fireEvent('message', item.data ['brukernavn']);
//        alert(item.data ['brukernavn']);
    });
});




myJsonUserSearchStore.on('message', function () {
  //myJsonUserSearchStore.reload();
  //myJsonUserSearchStore
  alert('query-test');
});