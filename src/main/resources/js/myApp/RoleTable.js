var xg = Ext.grid;
var roleIdx = 0;
var appIdx = 1;
var appIdxName = 1;

   var rsaveButtonHandler = function(button,event) {
        addRole();
        erdwin.hide();
        myJsonRoleDataStore.load();
   };
   var rcancelButtonHandler = function(button,event) {
        erdwin.hide();
        myJsonRoleDataStore.load();
   };
   function showModalRoleDetail() {
            erdwin.show();
   };

   var erdwin = new Ext.Window({
           layout:'fit',
           width:500,
           title:"Editer rolle",
           shim :false,
           modal:true,
           autoDestroy :true,
           monitorValid:true,
           closable:false,
           resizable:false,
           buttons: [{text:'OK', handler: rsaveButtonHandler}],
           items:[{ title:'Rolle detaljer',
                id: 'rolledetaljer',
                height:225,
                layout: 'fit',
                xtype: 'editrolepanel'
           }]
   });


var newRoleButtonHandler = function(button,event) {
        e_appid_field.setValue(appIdx);
        e_orgno_field.setValue('');
        e_organization_field.setValue('');
        e_roleName_field.setValue('');
        e_roleValue_field.setValue('');
        e_appName_field.setValue(appIdxName);
        showModalRoleDetail();
            
//   alert('You clicked the add role');
   };

var editRoleButtonHandler = function(button,event) {
        e_appid_field.setValue(myJsonRoleDataStore.data.items[roleIdx].get('appId'));
        e_orgno_field.setValue(myJsonRoleDataStore.data.items[roleIdx].get('orgID'));
        e_organization_field.setValue(myJsonRoleDataStore.data.items[roleIdx].get('organizationName'));
        e_roleName_field.setValue(myJsonRoleDataStore.data.items[roleIdx].get('roleName'));
        e_roleValue_field.setValue(myJsonRoleDataStore.data.items[roleIdx].get('roleValue'));
        e_appName_field.setValue(myJsonRoleDataStore.data.items[roleIdx].get('applicationName'));
        deleteRole();
        showModalRoleDetail();
        myJsonRoleDataStore.load();
   };
   
var deleteRoleButtonHandler = function(button,event) {
	 deleteRole();
   };

function deleteRole() {
     var myJson =  '{\"orgID\": \"'+myJsonRoleDataStore.data.items[roleIdx].get('orgID')+'\", \"roleName\": \"'+myJsonRoleDataStore.data.items[roleIdx].get('roleName')+'\", \"roleValue\": \"'+myJsonRoleDataStore.data.items[roleIdx].get('roleValue')+'\"}';

        Ext.Ajax.defaultHeaders = { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' };
        Ext.Ajax.request({
           url: myHostJsonRoleDelete+search_uid+'/'+appId+'/delete&jsond='+myJson,
	   	    method: 'GET',
	      	//jsonData: jsonStr,  // your json data
            //jsonData: Ext.encode(mjobj),
            success: function(transport){
                myJsonRoleDataStore.load();
            },
            failure: function(transport){
                alert("Fikk ikke slettet rollen: " - transport.responseText);
                myJsonRoleDataStore.load();
            }
        });
};

function addRole() {
	    var myJson =  '{\"orgID\": \"'+e_orgno_field.getValue()+'\", \"roleName\": \"'+e_roleName_field.getValue()+'\", \"roleValue\": \"'+e_roleValue_field.getValue()+'\"}';

        Ext.Ajax.defaultHeaders = { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' };
        Ext.Ajax.request({
           url: myHostJsonRoleAdd+search_uid+'/'+appId+'/add&jsond='+myJson,
	   	    method: 'GET',
	      	//jsonData: jsonStr,  // your json data
            //jsonData: Ext.encode(mjobj),
            success: function(transport){
                myJsonRoleDataStore.load();
            },
            failure: function(transport){
                alert("Fikk ikke oprettet rollen: " - transport.responseText);
                myJsonRoleDataStore.load();
            }
        });
};

var sm2 = new xg.CheckboxSelectionModel({
        listeners: {
            // On selection change, set enabled state of the removeButton
            // which was placed into the GridPanel using the ref config
            selectionchange: function(sm) {
                if (sm.getCount()) {
                    Ext.getCmp('rlrmbtn').enable();
                    Ext.getCmp('elrmbtn').enable();
                } else {
                    Ext.getCmp('rlrmbtn').disable();
                    Ext.getCmp('elrmbtn').disable();
                }
            }
        }
    });


RoleTable = Ext.extend(Ext.grid.GridPanel, {
  border:false,
  monitorValid:true,
  autoScroll:true,
  initComponent: function(config) {
      var config = {
              title: 'User roles and properties for the selected application',
              id: 'role-panel',
              //store: myRoleDataStore,
              store: myJsonRoleDataStore,
              viewConfig:{forceFit:true},
              sm: sm2,

              // Pass in a column model definition
              // Note that the DetailPageURL was defined in the record definition but is not used
              // here. That is okay.  selskap', 'rolle', 'verdier'
              columns: [
//                    {header: 'Selskap',  sortable: true, dataIndex: 'selskap'},
//                    {header: 'Rolle',  sortable: true, dataIndex: 'rolle'},
//                    {header: 'Egenskaper',  sortable: true, dataIndex: 'verdier'}
                    {header: 'Organization ID', size: 80, sortable: true, dataIndex: 'orgID'},
                    {header: 'Org name',  sortable: true, dataIndex: 'organizationName'},
                    {header: 'Role',  sortable: true, dataIndex: 'roleName'},
                    {header: 'Role properties',  sortable: true, dataIndex: 'roleValue'},
                    {header: 'Application', size: 80, sortable: true, dataIndex: 'applicationName'}
              ],

              tbar:[{
                    text:'New role',
                    tooltip:'Add a new role',
                    handler: newRoleButtonHandler,
                    iconCls:'add'
              }, '-', {
                    text:'Edit role',
                    id: 'elrmbtn',
                    tooltip:'Edit the role properties',
                    iconCls:'edit',
                    handler: editRoleButtonHandler,
                    // Place a reference in the GridPanel
                    ref: '../editButton',
                    disabled: true
              }, '-', {
                    text:'Delete role',
                    id: 'rlrmbtn',
                    tooltip:'Delete the role for the suer',
                    iconCls:'remove',
                    handler: deleteRoleButtonHandler,
                    // Place a reference in the GridPanel
                    ref: '../removeButton',
                    disabled: true
              },'-',{
                    text:'Show all',
                    tooltip:'Show all roles',
                    handler: removeFilter,
                    iconCls:'option'

              }],
              iconCls:'icon-grid'
      }
      Ext.apply(this, Ext.apply(this.initialConfig, config));
      RoleTable.superclass.initComponent.apply(this, arguments);
      var mySm2 = this.getSelectionModel();
      mySm2.on('rowselect', this.onRowSelect, this);


  },
  onRender:function() {
        //this.store.load();

        RoleTable.superclass.onRender.apply(this, arguments);
  }, // eo function onRender
   onRowSelect: function(sm, rowIdx, r) {
    // alert('rowSelected'+r.data)
    // getComponent will retrieve itemId's or id's. Note that itemId's
    // are scoped locally to this instance of a component to avoid
    // conflicts with the ComponentMgr
      roleIdx=rowIdx;
      appIdx=r.data.appId;
      appId=r.data.appId;

  }});

   var removeFilter = function(button,event) {
                      myJsonRoleDataStore.filter('appId', '');
   };

// This will associate an string representation of a class
// (called an xtype) with the Component Manager
// It allows you to support lazy instantiation of your components
Ext.reg('rolepanel', RoleTable);
