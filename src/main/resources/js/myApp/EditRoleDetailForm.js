  Bus = new Ext.util.Observable();


  var e_appid_field = new Ext.form.TextField({
        fieldLabel: 'Application ID ID',
        readOnly: true,
        name: 'appID',
        anchor:'-15'
    });

  var e_orgno_field = new Ext.form.TextField({
        fieldLabel: 'Organization ID',
        name: 'orgno',
        anchor:'-15'
    });

  var e_organization_field = new Ext.form.TextField({
        fieldLabel: 'Organization name',
        readOnly: true,
        name: 'orgName',
        anchor:'-15'
    });
  var e_roleName_field = new Ext.form.TextField({
        fieldLabel: 'Role',
        name: 'roleName',
        anchor:'-15'
    });
  var e_roleValue_field = new Ext.form.TextField({
        fieldLabel: 'Role properties',
        name: 'roleValue',
        anchor:'-15'
    });
  var e_appName_field = new Ext.form.TextField({
        fieldLabel: 'Application name',
        readOnly: true,
        name: 'AppName',
        anchor:'-15'
    });



EditRoleDetailForm = Ext.extend(Ext.form.FormPanel, {


    initComponent: function(config) {
        var config = {
            // Put your pre-configured config options here
            title: 'Rolleinformasjon',
            id: 'editrole-panel',
            bodyStyle:'padding:15px',
            monitorValid:true,
            autoScroll:true,
            labelWidth:170,
            //store: myJsonIdentityStore,
            layout: 'form',
            items: [ e_appid_field, e_appName_field, e_orgno_field, e_organization_field, e_roleName_field, e_roleValue_field ],
            iconCls:'icon-grid'

            }; // eo config
            Ext.apply(this, Ext.apply(this.initialConfig, config));
            EditRoleDetailForm.superclass.initComponent.apply(this, arguments);
            Bus.on('message', this.onMessage, this);
        }
        ,onRender:function() {
            EditRoleDetailForm.superclass.onRender.apply(this, arguments);
         }
        ,onShow:function(message) {

        }
   }
);


Ext.reg('editrolepanel', EditRoleDetailForm);
