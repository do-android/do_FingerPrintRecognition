package doext.module.do_FingerPrintRecognition.implement;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import core.DoServiceContainer;
import core.object.DoEventCenter;
import core.object.DoSingletonModule;

import org.json.JSONObject;

import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;
import doext.module.do_FingerPrintRecognition.define.do_FingerPrintRecognition_IMethod;
import doext.module.do_FingerPrintRecognition.app.do_FingerPrintRecognition_App;

/**
 * 自定义扩展SM组件Model实现，继承DoSingletonModule抽象类，并实现do_FingerPrintRecognition_IMethod接口方法；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象；
 * 获取DoInvokeResult对象方式new DoInvokeResult(this.getUniqueKey());
 */
public class do_FingerPrintRecognition_Model extends DoSingletonModule implements do_FingerPrintRecognition_IMethod {

    public do_FingerPrintRecognition_Model() throws Exception {
        super();
    }

    /**
     * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
     *
     * @_methodName 方法名称
     * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
     * @_scriptEngine 当前Page JS上下文环境对象
     * @_invokeResult 用于返回方法结果对象
     */
    @Override
    public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas,
                                    DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult)
            throws Exception {
        if ("startRecognize".equals(_methodName)) {
            this.startRecognize(_dictParas, _scriptEngine, _invokeResult);
            return true;
        }
        return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
    }

    /**
     * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用，
     * 可以根据_methodName调用相应的接口实现方法；
     *
     * @_methodName 方法名称
     * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
     * @_scriptEngine 当前page JS上下文环境
     * @_callbackFuncName 回调函数名
     * #如何执行异步方法回调？可以通过如下方法：
     * _scriptEngine.callback(_callbackFuncName, _invokeResult);
     * 参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
     * 获取DoInvokeResult对象方式new DoInvokeResult(this.getUniqueKey());
     */
    @Override
    public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas,
                                     DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
        return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
    }

    @Override
    public void startRecognize(JSONObject _dictParas, DoIScriptEngine _scriptEngine, final DoInvokeResult _invokeResult) throws Exception {
        FingerprintUtil.callFingerPrint(new FingerprintUtil.OnCallBackListenr() {
            @Override
            public void onSupportFailed() {
                _invokeResult.setResultBoolean(false);
                DoServiceContainer.getLogEngine().writeInfo("当前设备不支持指纹", "do_FingerPrintRecognition startRecognize");
            }

            @Override
            public void onInsecurity() {
                _invokeResult.setResultBoolean(false);
                DoServiceContainer.getLogEngine().writeInfo("当前设备未处于安全保护中", "do_FingerPrintRecognition startRecognize");
            }

            @Override
            public void onEnrollFailed() {
                _invokeResult.setResultBoolean(false);
                DoServiceContainer.getLogEngine().writeInfo("请到设置中设置指纹", "do_FingerPrintRecognition startRecognize");
            }

            @Override
            public void onAuthenticationStart() {
                _invokeResult.setResultBoolean(true);
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                fireEnevt(false);
                DoServiceContainer.getLogEngine().writeInfo(errString.toString(), "do_FingerPrintRecognition startRecognize");
            }

            @Override
            public void onAuthenticationFailed() {
                fireEnevt(false);
                DoServiceContainer.getLogEngine().writeInfo("解锁失败", "do_FingerPrintRecognition startRecognize");
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                fireEnevt(false);
                DoServiceContainer.getLogEngine().writeInfo(helpString.toString(), "do_FingerPrintRecognition startRecognize");
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                fireEnevt(true);
            }
        });
    }


    private void fireEnevt(boolean result) {
        DoInvokeResult _invokeResult = new DoInvokeResult(getUniqueKey());
        _invokeResult.setResultBoolean(result);
        DoEventCenter eventCenter = getEventCenter();
        if (eventCenter != null) {
            eventCenter.fireEvent("recognizeResult", _invokeResult);
        }
    }
}