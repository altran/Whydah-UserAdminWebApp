UserTable = Ext.extend(Ext.grid.GridPanel, {
  border:false,
  initComponent: function(config) {
      var config = {
              title: 'Registrerte brukere',
              id: 'usertable-panel',
              store: myUserDataStore,
              viewConfig:{forceFit:true},
              sm: new Ext.grid.RowSelectionModel({singleSelect: true}),

              // Pass in a column model definition
              // Note that the DetailPageURL was defined in the record definition but is not used
              // here. That is okay.
              columns: [
                    {header: 'Brukernavn', width: 120, sortable: true, dataIndex: 'username'},
                    {header: 'Fornavn', width: 90, sortable: true, dataIndex: 'fornavn'}
              ]
      }
      Ext.apply(this, Ext.apply(this.initialConfig, config));
      UserTable.superclass.initComponent.apply(this, arguments);

  },
  onRender:function() {
        //this.store.load();

        UserTable.superclass.onRender.apply(this, arguments);
  } // eo function onRender

});
// This will associate an string representation of a class
// (called an xtype) with the Component Manager
// It allows you to support lazy instantiation of your components
Ext.reg('usertablepanel', UserTable);
