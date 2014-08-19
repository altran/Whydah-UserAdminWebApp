  Bus = new Ext.util.Observable();

  var resObj;
  var uniqueueUID=false;
  Ext.form.VTypes.nameText = 'In-valid.';
  Ext.form.VTypes.name   = function(v){
      return uniqueueUID;
  };


  var e_uid_field = new Ext.form.TextField({
        fieldLabel: 'User ID',
        name: 'uid',
        anchor:'-15',
        vtype: 'name',
        enableKeyEvents:true,
        listeners:{
                keypress: function(field, event) {
                    // alert('key: '+field.getValue()+String.fromCharCode(event.getKey()));
                    var queryString = e_uid_field.getValue()+String.fromCharCode(event.getKey());
                    reg = /\s+/;  // users/exists/{uid}")
                    query2 = queryString.replace(reg,'_')
                    var conn = new Ext.data.Connection();
                    conn.request({
                        url:        myHostJsonUsers+query2+'/exists/',
                        method:     'GET',
                        success: function(responseObject) {
                            var patt1=/false/gi;
                            if (responseObject.responseText.match(patt1)) {
                                uniqueueUID=true;
                            } else {
                                uniqueueUID=false;
                            }


                        },
                        failure: function() {
            //                Ext.MessageBox.alert('Something failed:'+url);
                        }
                    });
                }
        }

    });

  var e_brukernavn_field = new Ext.form.TextField({
        fieldLabel: 'User name',
        name: 'username',
        anchor:'-15'
    });

  var e_firstName_field = new Ext.form.TextField({
        fieldLabel: 'First name',
        name: 'firstName',
        anchor:'-15'
    });
  var e_lastName_field = new Ext.form.TextField({
        fieldLabel: 'Last name',
        name: 'lastName',
        anchor:'-15'
    });
  var e_email_field = new Ext.form.TextField({
        fieldLabel: 'Email',
        name: 'email',
        anchor:'-15'
    });
  var e_cell_field = new Ext.form.TextField({
        fieldLabel: 'Callphone',
        name: 'cellPhone',
        anchor:'-15'
    });



EditUserDetailForm = Ext.extend(Ext.form.FormPanel, {


    initComponent: function(config) {
        var config = {
            // Put your pre-configured config options here
            title: 'User information',
            id: 'bruker-panel',
            bodyStyle:'padding:15px',
            monitorValid:true,
            autoScroll:true,
            labelWidth:70,
            store: myJsonIdentityStore,
            layout: 'form',
            items: [ e_uid_field,e_brukernavn_field, e_firstName_field, e_lastName_field, e_email_field, e_cell_field ],
            iconCls:'icon-grid'

            }; // eo config
            Ext.apply(this, Ext.apply(this.initialConfig, config));
            EditUserDetailForm.superclass.initComponent.apply(this, arguments);
            Bus.on('message', this.onMessage, this);
        }
        ,onRender:function() {
            EditUserDetailForm.superclass.onRender.apply(this, arguments);
         }
        ,onShow:function(message) {

        }
   }
);


Ext.reg('edituserpanel', EditUserDetailForm);
