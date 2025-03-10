package aeac.sys.aeac_basetemple.utils;

import kd.bos.dataentity.TypesContainer;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {
    private static Map<String, String> serviceMap = new HashMap<String, String>();
    static {
        serviceMap.put("VoucherDataService", "kd.cus.eip.VoucherDataServiceImp");
    }

    public static Object getService(String serviceName) {
        String className = (String)serviceMap.get(serviceName);

//        if(className == null) {
//            throw new RuntimeException(String.format("%s对应的服务实现未找到", new Object[]{serviceName}));
//        } else {
//            return TypesContainer.getOrRegisterSingletonInstance(className);
//        }
        return null;
    }
}
