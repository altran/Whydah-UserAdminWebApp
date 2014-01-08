var query2;
UserSearchForm = Ext.extend(Ext.form.FormPanel, {

    // Put your pre-configured config options here
    initComponent: function(config) {
        var config = {
            title: 'User search',
            labelAlign: 'top',
            labelWidth:70,
            layout: 'table',
            width: 800,
            items:  [
//                {
//                       xtype: 'spacer',
//                       width: 455
//                },
                {
                       xtype: 'textfield',
                       fieldLabel: 'User name',
                       name: 'userName',
                       value: '',
                       width:225,
                       enableKeyEvents:true,
                       listeners:{
                             	keyup: function(field, event) {
                                    var queryString = field.getValue();
                                    if(queryString == ""){
                                        queryString = '*';
                                    }
                                    queryString = encodeURI(queryString);
                                    myJsonUserSearchStore.proxy.conn.url = myHostJsonUserFind+''+queryString;
                                    myJsonUserSearchStore.load();
                                }
                        }
                },
                {
                        xtype: 'button',
                        text: 'Search',
                        width: 100
                }
            ]

        }
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        UserSearchForm.superclass.initComponent.apply(this, arguments);
    }

});
function empty(e)
{
    switch(e) {
        case "":
        case 0:
        case "0":
        case null:
        case false:
        case typeof this == "undefined":
            return true;
                default : return false;
    }
}
Ext.reg('searchpanel', UserSearchForm);

