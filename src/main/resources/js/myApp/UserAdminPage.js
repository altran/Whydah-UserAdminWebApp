UserAdminPage = Ext.extend(Ext.Panel, {

  initComponent: function(config) {
      var config = {
            id:'main-panel',
            //width: 810,
            baseCls:'x-plain',
            renderTo: Ext.getBody(),
            layout: {
                type: 'table',
                columns: 2
            },
            // applied to child components
            defaults: {frame:true, width:400, height: 200},
            items:[{
                title: appTitle,
//                border: false,
//                frame:false,
                id: 'topptekst',
                layout: 'fit',
                xtype: 'searchpanel',
                height:60,
                width: 800,
                colspan:2
            },{
                title:'Users',
                id: 'brukere',
                layout: 'fit',
                xtype: 'usersearchresult',
                width: 800,
                colspan:2
            }, { title:'User details',
                id: 'brukerdetaljer',
                height:225,
                layout: 'fit',
                xtype: 'userpanel'
            },{ title:'Available applications',
                id: 'applikasjoner',
                height:225,
                layout: 'fit',
                xtype: 'apppanel'
            },{ title:'Organizations, roles and parametres for the application',
                id: 'roller',
                layout: 'fit',
                xtype: 'rolepanel',
                width: 800,
                height:300,
                colspan:2
            }]



      }
      Ext.apply(this, Ext.apply(this.initialConfig, config));
      UserAdminPage.superclass.initComponent.apply(this, arguments);
  }
});



// This will associate an string representation of a class
// (called an xtype) with the Component Manager
// It allows you to support lazy instantiation of your components
Ext.reg('frontpage', UserAdminPage);

