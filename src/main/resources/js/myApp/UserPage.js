UserPage = Ext.extend(Ext.Panel, {

  initComponent: function(config) {
      var config = {

          layout: 'border',
          width:800,
          height:400,
          id:'maine-panel',
          renderTo: Ext.getBody(),
          items: [{
                 region: 'west',
                 xtype: 'userform2',
                 layout:'fit',
                 width: 400
              },{
                 region: 'center',
                 xtype: 'userform',
                 layout:'fit',
                 height: 300
              }
          ]
      }
      Ext.apply(this, Ext.apply(this.initialConfig, config));
      UserPage.superclass.initComponent.apply(this, arguments);
  }
});
  


// This will associate an string representation of a class
// (called an xtype) with the Component Manager
// It allows you to support lazy instantiation of your components
Ext.reg('userpage', UserPage);

