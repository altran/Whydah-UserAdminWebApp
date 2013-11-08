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
                {
                       xtype: 'spacer',
                       width: 455
                },
                {
                       xtype: 'textfield',
                       fieldLabel: 'User name',
                       name: 'userName',
                       value: '',
                       width:225,
                       enableKeyEvents:true,
                       listeners:{
                             	keypress: function(field, event) {
                                    // alert('key: '+field.getValue()+String.fromCharCode(event.getKey()));
                                    var queryString = field.getValue()+String.fromCharCode(event.getKey());
                                    reg = /\s+/;
                                    query2 = queryString.replace(reg,'_')
                                    myJsonUserSearchStore.proxy.conn.url = myHostJsonUserFind+''+query2;
                                    myJsonUserSearchStore.load();
                                    //Bus.fireEvent('message', field.getValue()+String.fromCharCode(event.getKey()))
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

Ext.reg('searchpanel', UserSearchForm);

