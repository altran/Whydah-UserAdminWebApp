

var myJsonPersonCustomerStore =  new Ext.data.JsonStore({
//   url:  myHostJson+'?url=http://localhost:9999/members/',
   url:  myHostJsonMembers,
//        url: './json',
//        url: './jsondata',
        root: 'members',
        autoLoad: true,
        autoSave: false,
        id : 'id',

        fields: [
            'id', 'firstname', 'lastname','birthday','city','country','email','picture_uri','birthdate',
            'gender'
        ]


});

 function renderUrl(value, p, record){
        return String.format('<strong><a href="{1}" title="{3}">{2}</a></strong>', value, record.data.url_path, record.id, record.data.name);
 }


myJsonPersonCustomerStore.on('load', function () {
    myJsonPersonCustomerStore.data.each(function(item, index, totalItems ) {
       // Bus.fireEvent('message', item.data ['brukernavn']);
  //      Bus.fireEvent('message', item.data ['brukernavn']);
//        alert(item.data ['brukernavn']);
    });
});




myJsonPersonCustomerStore.on('message', function () {
  //myJsonUserSearchStore.reload();
  //myJsonUserSearchStore
  alert('query-test');
});