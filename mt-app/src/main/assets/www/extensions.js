function isInteger(num){
    return Math.floor(num) === num;
}

function isPositiveInteger(num){
    var parnt = /^[1-9]\d*(\.\d+)?$/;
    if(parnt.exec(num)){
        return true;
    }
    return false;
}

function isChOrEn(str){
    if(!(Math.floor(str) === str)){
        var isNum = /[0-9]/;
        if(isNum.test(str)){
            return false;
        }else{
            return true;
        }
    }
    return false;
}

function isEn(str){
    var reg=new RegExp("^[a-zA-Z]+$");
    if(reg.test(str)){
        return true;
    }
    return false;
}

function isCh(str){
    var reg = new RegExp("[\\u4E00-\\u9FFF]+","g");
    if(reg.test(str)){
        return true;
    }
    return false;
}

function isNum(val){
    var regPos = /^\d+(\.\d+)?$/; //非负浮点数
    var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
    if(regPos.test(val) || regNeg.test(val)){
        return true;
    }else{
        return false;
    }
}

(function CORE_EXTENSION(CTRL, D) {

	function _inputed(v, def) {
		var n = 0;
		if(v) {
			for(var i in v) {
				if(v.hasOwnProperty(i)) {
					var optv = v[i];
					if(def) {
						if(def == optv) {
							n++;
						}
					} else if(optv != undefined) {
						n++;
					}
				}
			}
		}
		return n;
	}

	function _answered(data, qid) {
		var required = data.qprop(qid, 'required');
		var visible = data.qvisible(qid);
		var total = 0, n = 0;
		if(required !== true && visible !== false) {
			total = 1;
			var v = data.qval(qid);
			var type = data.qprop(qid, 'type');
			switch(type) {
			case RS2_T_SELECT:
			case RS2_T_MULSELECT:
				n = _inputed(v, '1') > 0 ? 1 : 0;
				break;
			case RS2_T_ASSIGNMENT: {
				var opts = data.qprop(qid, 'options');
				if(opts) {
					total = opts.length;
					n = _inputed(v);
				}
			}
				break;
			case RS2_T_LABEL:
			case RS2_T_UNKNOWN: {
				total = 0;
			}
				break;
			default:
				var vt = typeof v;
				if(vt === 'object') {
					n =  _inputed(v) > 0 ? 1 : 0;
 				} else if(vt === 'string') {
					n = v.length > 0 ? 1 : 0;
				} else if (v !== undefined) {
					n = 1;
				}
				break;
			}
		}
		return [total, n];
	}

	CTRL.on_event('page_finished', function(state, data) {
		var hist = state.history();
		var total = 0;
		var n = 0;
		var result = 1;
		var qids = [];

		function _append(r0, r1, qid) {
			total += r0;
			n += r1;
			if(r0 > 0 && r1 === 0) {
				qids.push(qid);
			}
		}

		for(var i = 0; i < hist.length; ++i) {
			var qid = hist[i].qid;
			var type = data.qprop(qid, 'type');
			if(type === RS2_T_GROUP) {
				var subqs = data.qprop(qid, 'questions');
				for(var j = 0; j < subqs.length; ++j) {
					var subqid = subqs[j];
					var r = _answered(data, subqid);
					_append(r[0], r[1], subqid);
				}
			} else {
				var r = _answered(data, qid);
				_append(r[0], r[1], qid);
			}
		}
		if(total > 0 && n < total) {
			result = n / total;
		}
		data.set_sys_vval("completion_rate", result);
		data.set_sys_vval("completion_rate_total", total);
		data.set_sys_vval("completion_rate_inputed", n);
		data.set_sys_vval("completion_rate_qids", qids);
	});

	
	D.define_func('empty_questions', function() {
		var qids = this.val("__sys_completion_rate_qids") || [];
		var msg = "未答 " + qids.length +" 题: " + qids.join(", ");
		return msg;
	});	
}(RS2_CORE_SURVEY_CTRL, RS2_CORE_DATA));
