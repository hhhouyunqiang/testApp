var RS2_M_PLUGIN = (function(U){
	

	function _create(handler) {
		var _callbackid = 0;
		var _callbacks = {};

		function _new_cid() {
			++_callbackid;
			return _callbackid;
		}

		if (!window.console) {
			window.console = {
				log : function (msg){
					_self.call("log", [msg]);
				},

				assert : function (){}
			};
		}

		var _calc;
		

		var _self = {
			set_calc : function(calc) {
				_calc = calc;
			},

			call : function(name, params, callback) {
				var cid = _new_cid();
				if(callback) {
					_callbacks[cid] = callback;
				}
				if(name == "calc") {
					console.log("calc " + _calc);
					if(_calc) {
						console.log("show calc");
						_calc.show(callback);
					}
				} else if(name === "nextPageLoaded") {
          var d = handler.submit_data();
          params.push(JSON.stringify(d.state));
          params.push(JSON.stringify(d.data));
        }
				
				if(window.jsbridge) {
					jsbridge.postMessage(cid, name, JSON.stringify(params||[]));
				} else if(window.webkit) {
					webkit.messageHandlers.rs2_plugin.postMessage({
						callbackid : cid,
						name : name,
						params : params
					});
				}
			},

			callback : function(cid, r, params) {
				var f = _callbacks[cid];
				if(f) {
					delete _callbacks[cid];
					U.call_async(function(){
						f(r, params)
					});
				}
			},

            load_remote : function(url) {
                $.getScript(url, function(data, textStatus, jqxhr) {
                    U.info("survey loaded at " + url + " " + textStatus + " , "
                							+ jqxhr.status);
                	_self.start()
                    });
            },

            start : function(state, data, interview, vars, externs) {
				handler.start({state:state, 
							   data:data,
							   variables:vars,
							   interview:interview,
							   externs:externs});
            },

			page_event : function(name) {
			    if (document.activeElement) {
            	    document.activeElement.blur();
            	}
				handler.event({name:name})
			},

			submit : function() {
				var d = handler.submit_data();
				var arr = [JSON.stringify(d.state), JSON.stringify(d.data)];
				var extra = this.extra_submit_data(d);
				if(extra != undefined) {
					arr.push(extra);
				}
				this.call('submitData', arr); 
			},

			save : function() {
			    if (document.activeElement) {
            		document.activeElement.blur();
            	}
				var d = handler.submit_data();
				var arr = [JSON.stringify(d.state), JSON.stringify(d.data)];
				var extra = this.extra_submit_data(d);
				if(extra != undefined) {
					arr.push(extra);
				}
				this.call('saveData', arr); 
			},

			toggle_menu : function() {
			    handler.toggle_menu();
			},

			toggle_remark : function() {
			    handler.toggle_remark();
			},

			extra_submit_data : function(d) {
				var extra = {};
				extra.estatus = d.data.estatus;
				var sysextra = d.data.var_dict["__sys_submit_extra"];
				if(sysextra) {
					extra.submit_extra = sysextra;
				}
				var tags = d.data.var_dict["__sys_sys_tags"];
				if(tags) {
					extra.tags = tags;
				}
				return extra;
			},

			end_editing : function() {
			    if (document.activeElement) {
                    document.activeElement.blur();
                }
			},

			on_swipe : function(d) {
				if(d === 1) {
					handler.hide_menu();
				}
			}
		};
		return _self;
	}

	var _m = {
		create : _create
	};
	return _m;
}(RS2_UTIL));
