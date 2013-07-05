var fornavn;
var etternavn;
var pref;
var newUrl;

   var findCustomerButtonHandler = function(button,event) {
//	    alert('You clicked the new password button!');
        newUrl = myHostJson+'?url=http://localhost:9999/members/';
        myJsonPersonCustomerStore.proxy.conn.url = newUrl;
        myJsonPersonCustomerStore.load();
        cswin.show();



   };

    var cswin = new Ext.Window({
           layout:'fit',
           width:500,
           title:"Finn kunde",
           shim :false,
           modal:true,
           autoDestroy :true,
           monitorValid:true,
           closable:true,
           resizable:true,
           items:[{ title:'Kunder',
                id: 'rolledetaljer',
                height:225,
                layout: 'fit',
                xtype: 'customersearchresult'
           }]
   });

   var newPWButtonHandler  = function(button,event) {
//	    alert('You clicked the new password button!');

	    var conn = new Ext.data.Connection();
        conn.request({
            url:        myHostJsonUsers+search_uid+'/resetpassword',
            method:     'GET',
            success: function(responseObject) {
                Ext.MessageBox.alert(search_uid+' har fått tilsendt nytt passord.');
            },
            failure: function() {
                Ext.MessageBox.alert('Something failed:'+url);
            }
        });
        Ext.MessageBox.alert(search_uid+' har fått tilsendt nytt passord ');

   };


UserSearchResultForm  = Ext.extend(Ext.grid.GridPanel, {
  border:false,
  monitorValid:true,
  autoScroll:true,
  initComponent: function(config) {
      var config = {
              title: 'Brukere',
              id: 'brukersok-panel',
//              store: myUserDataStore,,
              store: myJsonUserSearchStore,
              viewConfig:{forceFit:true},
              sm: new Ext.grid.RowSelectionModel({singleSelect: true}),
              //sm: new Ext.grid.CheckboxSelectionModel({});


              // Pass in a column model definition
              // Note that the DetailPageURL was defined in the record definition but is not used
              // here. That is okay.   id: 'company'
             cm: new xg.ColumnModel([
                    sm2,
                    {header: 'Brukernavn', size: 100, sortable: true, dataIndex: 'username'},
                    {header: 'Fornavn', size: 100, sortable: true, dataIndex: 'firstName'},
                    {header: 'Etternavn', size: 100, sortable: true, dataIndex: 'lastName'},
                    {header: 'Mobil', size: 100, sortable: true, dataIndex: 'cellPhone'},
                    {header: 'Kundereferanse', size: 100, sortable: true, dataIndex: 'personRef'}
              ]),
              tbar: [ {
                          text: 'Lag nytt passord',
                          minWidth: 100,
                          iconCls:'password',
                          handler: newPWButtonHandler,
                          ref: '../newPwButton'
                      }, {
                          text: 'Vis kundeinformasjon',
                          minWidth: 100,
                          iconCls:'customer',
                          disabled : true,
                          ref: '../customerButton'
                      }, {
                          text: 'Knytt bruker til kunde',
                          minWidth: 100,
                          iconCls:'connect',
                          disabled : false,
                          handler: findCustomerButtonHandler,
                          ref: '../connectButton'
                      }, {
                          text: 'Fjern kundereferanse',
                          minWidth: 100,
                          iconCls:'cross',
                          disabled : true,
                          ref: '../removeConnectionButton'
                      }
              ],
              iconCls:'icon-grid'


      };
      Ext.apply(this, Ext.apply(this.initialConfig, config));
      UserSearchResultForm.superclass.initComponent.apply(this, arguments);

        // now add application specific events
        // notice we use the selectionmodel's rowselect event rather
        // than a click event from the grid to provide key navigation
        // as well as mouse navigation
        var mySm = this.getSelectionModel();
        mySm.on('rowselect', this.onRowSelect, this);

  },
  onRender:function() {
        //this.store.load();

        UserSearchResultForm.superclass.onRender.apply(this, arguments);
  }, // eo function onRender
  onRowSelect: function(sm, rowIdx, r) {
        search_uid = r.data.username;
        fornavn = r.data.firstName;
        etternavn = r.data.lastName;
        pref = r.data.personRef;
        newUrl = myHostJsonUsers+search_uid;
        myJsonIdentityStore.proxy.conn.url = newUrl;
        myJsonIdentityStore.load();
        myJsonApplicationDataStore.proxy.conn.url = newUrl+'/applications';
        myJsonApplicationDataStore.load();
        myJsonRoleDataStore.proxy.conn.url = newUrl;
        myJsonRoleDataStore.load();


   }
});

// This will associate an string representation of a class
// (called an xtype) with the Component Manager
// It allows you to support lazy instantiation of your components
Ext.reg('usersearchresult', UserSearchResultForm);


