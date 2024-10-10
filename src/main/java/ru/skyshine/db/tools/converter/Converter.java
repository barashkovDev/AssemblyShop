package ru.skyshine.db.tools.converter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Converter {

    /**
     * Вернуть 2мерный массив данных
     *
     * @param obj объектное представление данных одной модели для 1 экземпляра
     * @param fields поля для выборки (null - все поля)
     * @return String[][] массив данных, где строка - объект, столбец - атрибут
     */
    public static String[][] objToString(Object obj, List<String> fields) {
        return objToString(List.of(obj), fields);
    }

    /**
     * Вернуть 2мерный массив данных
     *
     * @param obj объектное представление данных одной модели для нескольких экземпляров
     * @param fields поля для выборки (null - все поля)
     * @return String[][] массив данных, где строка - объект, столбец - атрибут
     */
    public static String[][] objToString(List<?> obj, List<String> fields) {
        if (obj == null || obj.isEmpty() || !obj.get(0).getClass().getName().contains("ru.skyshine.db.model"))
            return null;
        List<String> specificFields = (fields == null) ? allFields(obj.get(0).getClass()) : fields;
        return resultObjToString(obj, specificFields);
    }

    /**
     * Вернуть все столбцы в том же порядке, что указано в модели
     *
     * @param clazz модель таблицы
     * @return List<String> наименование столбцов в соответствии с названием полей в модели. Если это внешний ключ,
     * то через точку пишется путь до примитива (не внешнего ключа)
     */
    public static List<String> allFields(Class<?> clazz) {
        return recFillFields("", clazz, new ArrayList<>());
    }

    //todo получения объекта по его пути через точки
    @SuppressWarnings("unused")
    public static Object objByDeref(String pathDeref) {
        return objByDeref(pathDeref, null);
    }

    @SuppressWarnings("unused")
    public static Object objByDeref(String pathDeref, Integer depth) {
        return null;
    }

    private static List<String> recFillFields(String elemPath, Class<?> clazz, List<String> str) {
        List<Field> declaredFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(x -> Modifier.isPrivate(x.getModifiers()))
                .toList();
        for (Field curField : declaredFields) {
            Class<?> curClazz = curField.getType();
            String curElemPath = elemPath + curField.getName();
            if (curClazz.getName().contains("ru.skyshine.db.model"))
                recFillFields(curElemPath + '.', curClazz, str);
            else
                str.add(curElemPath);
        }
        return str;
    }

    private static String[][] resultObjToString(List<?> obj, List<String> fields) {
        Method method;
        Object data;
        String[] nameMethod;
        String[][] result = new String[obj.size()][fields.size()];
        try {
            for (int field = 0; field < fields.size(); field++) {
                for (int elem = 0; elem < obj.size(); elem++) {
                    nameMethod = fields.get(field).split("\\.");
                    for (int i = 0; i < nameMethod.length; i++) {
                        nameMethod[i] = "get" + nameMethod[i].substring(0, 1).toUpperCase() + nameMethod[i].substring(1);
                    }

                    Object curObject = obj.get(elem);
                    for (int i = 0; i < nameMethod.length - 1; i++) {
                        method = obj.get(i).getClass().getMethod(nameMethod[i]);
                        curObject = method.invoke(curObject);
                    }
                    method = curObject.getClass().getMethod(nameMethod[nameMethod.length - 1]);
                    data = method.invoke(curObject);
                    result[elem][field] = (data != null) ? String.valueOf(data) : "";
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
