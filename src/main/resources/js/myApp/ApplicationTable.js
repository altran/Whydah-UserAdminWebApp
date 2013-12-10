var axg = Ext.grid;

 var newAppButtonHandler = function(button,event) {
	    alert('You clicked the add app CHECKBOX!');


   };

 var removeAppButtonHandler = function(button,event) {
	    alert('You clicked the remove app button!');

   };


    var checkColumn = new Ext.grid.CheckColumn({
       header: 'Access',
       click: true,
       dataIndex: 'hasRoles',
       sortable: true,
       listeners: { 'change':function(value) { alert(value)}},
       width: 55
    });
    checkColumn.on('click', function(element, evt, record) {
        appId=record.get('appId');
        if (record.get('hasRoles') == true ) {
            var conn = new Ext.data.Connection();
            conn.request({
                url:        myHostJsonUsers+search_uid+'/'+record.get('appId')+'/adddefaultrole',
                method:     'GET',
                success: function(responseObject) {
    //                Ext.MessageBox.alert(search_uid+' har fått tilsendt nytt passord.');
                },
                failure: function() {
    //                Ext.MessageBox.alert('Something failed:'+url);
                }
            });

        } else {
//            alert('Access removed');
            var conn = new Ext.data.Connection();
            conn.request({
                url:        myHostJsonUsers+search_uid+'/'+record.get('appId')+'/deleteall',
                method:     'GET',
                success: function(responseObject) {
    //                Ext.MessageBox.alert(search_uid+' har fått tilsendt nytt passord.');
                },
                failure: function() {
    //                Ext.MessageBox.alert('Something failed:'+url);
                }
            });
        }
        myJsonRoleDataStore.proxy.conn.url = newUrl;
        myJsonRoleDataStore.load();
        myJsonRoleDataStore.filter('appId', record.get('appId'));
        appId=record.get('appId');

    });

    var cm = new Ext.grid.ColumnModel([
        checkColumn,
        {
           header: 'Application name',
           id: 'applicationName',
           dataIndex: 'applicationName',
           sortable: true,
           width: 100
        },{
           header: 'App ID',
           dataIndex: 'appId',
           sortable: true,
           width: 100
        }

    ]);

var asm2 = new axg.CheckboxSelectionModel({
        listeners: {
            // On selection change, set enabled state of the removeButton
            // which was placed into the GridPanel using the ref config
            selectionchange: function(sm) {
                if (sm.getCount()) {
//                    Ext.getCmp('rarmbtn').enable();
//                    Ext.getCmp('earmbtn').enable();
                } else {
                    Ext.getCmp('rarmbtn').disable();
                    Ext.getCmp('earmbtn').disable();
                }
            },
            rowselect : function( selectionModel, rowIndex, record){
                myJsonRoleDataStore.proxy.conn.url = newUrl;
                myJsonRoleDataStore.load();
                myJsonRoleDataStore.filter('appId', record.get('appId'));
            }

        }
    });


var rec;
var editing = 'false';

//ApplicationTable = Ext.extend(Ext.grid.GridPanel, {
  border:false,
  autoScroll:true,
  initComponent: function(config) {
      var config = {
              title: 'Whydah application access',
              id: 'applikasjon-panel',
              autoExpandColumn: 'applicationName',
              store: myJsonApplicationDataStore,
              //viewConfig:{forceFit:true},
              sm: asm2,
              cm: cm,
              plugins: checkColumn,
              clicksToEdit: 1,
//              columns: [
//                    {header: 'Tilgang', type: 'bool', sortable: true, dataIndex: 'hasRoles'},
//                    {header: 'Applikasjonsnavn',  sortable: true, dataIndex: 'applicationName'},
//                    {header: 'ApplikasjonsID',  sortable: true, dataIndex: 'appId'}
//              ],
              tbar:[{
                    text:'New application',
                    tooltip:'Add a new Whydah application',
                    disabled : false,
                    iconCls:'add'
              }, {
                    text:'Edit',
                    id: 'earmbtn',
                    tooltip:'Edit application',
                    iconCls:'edit',
                    // Place a reference in the GridPanel
                    ref: '../editButton',
                    disabled: true
              }, {
                    text:'Delete',
                    id: 'rarmbtn',
                    tooltip:'Delete application',
                    iconCls:'remove',
                    // Place a reference in the GridPanel
                    ref: '../removeButton',
                    disabled: true
              }],
              iconCls:'icon-grid'

      }
      Ext.apply(this, Ext.apply(this.initialConfig, config));
      ApplicationTable.superclass.initComponent.apply(this, arguments);
      var mySmA = this.getSelectionModel();
      mySmA.on('rowselect', this.onRowSelect, this);
  },
  onRender:function() {

        ApplicationTable.superclass.onRender.apply(this, arguments);
  },// eo function onRender
     onRowSelect: function(sm, rowIdx, r) {
        appId=r.get('appId');
        myJsonRoleDataStore.proxy.conn.url = newUrl;
        myJsonRoleDataStore.load();
        myJsonRoleDataStore.filter('appId', r.get('appId'));
      // alert('rowSelected'+r.data)
      // getComponent will retrieve itemId's or id's. Note that itemId's
      // are scoped locally to this instance of a component to avoid
      // conflicts with the ComponentMgr
  },// eo function onRender
  onRowClick : function(sm, rowIdx, r) {
        appId=r.get('appId');
  }

});

// This will associate an string representation of a class
// (called an xtype) with the Component Manager
// It allows you to support lazy instantiation of your components
Ext.reg('apppanel', ApplicationTable);
