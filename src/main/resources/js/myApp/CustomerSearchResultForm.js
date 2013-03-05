
var newUrl;



CustomerSearchResultForm  = Ext.extend(Ext.grid.GridPanel, {
  border:false,
  monitorValid:true,
  autoScroll:true,
  initComponent: function(config) {
      var config = {
              title: 'Kunder',
              id: 'kunder-panel',
//              store: myUserDataStore,,
              store: myJsonPersonCustomerStore,
              viewConfig:{forceFit:true},
              sm: new Ext.grid.RowSelectionModel({singleSelect: true}),
              //sm: new Ext.grid.CheckboxSelectionModel({});


              // Pass in a column model definition
              // Note that the DetailPageURL was defined in the record definition but is not used
              // here. That is okay.   id: 'company'
             cm: new xg.ColumnModel([
                    sm2,
                    {header: 'BrukerID', size: 100, sortable: true, dataIndex: 'id'},
                    {header: 'Fornavn', size: 100, sortable: true, dataIndex: 'firstname'},
                    {header: 'Etternavn', size: 100, sortable: true, dataIndex: 'lastname'},
                    {header: 'Fødselsdag', size: 100, sortable: true, dataIndex: 'birthday'},
                    {header: 'By', size: 100, sortable: true, dataIndex: 'city'}

              ]),
              tbar: [ {
                          text: 'Koble bruker til denne kunden',
                          minWidth: 100,
                          iconCls:'connect',
                          ref: '../newPwButton'
                      }
              ],
              iconCls:'icon-grid'


      }
      Ext.apply(this, Ext.apply(this.initialConfig, config));
      CustomerSearchResultForm.superclass.initComponent.apply(this, arguments);

        // now add application specific events
        // notice we use the selectionmodel's rowselect event rather
        // than a click event from the grid to provide key navigation
        // as well as mouse navigation
        var mySm = this.getSelectionModel();
        mySm.on('rowselect', this.onRowSelect, this);

  },
  onRender:function() {
        //this.store.load();

        CustomerSearchResultForm.superclass.onRender.apply(this, arguments);
  }, // eo function onRender
  onRowSelect: function(sm, rowIdx, r) {


   }
});

// This will associate an string representation of a class
// (called an xtype) with the Component Manager
// It allows you to support lazy instantiation of your components
Ext.reg('customersearchresult', CustomerSearchResultForm);


