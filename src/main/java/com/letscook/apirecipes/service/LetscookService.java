package com.letscook.apirecipes.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letscook.apirecipes.config.ConfigProperties;
import com.letscook.apirecipes.dto.DataDTO;
import com.letscook.apirecipes.dto.RecipesDTO;
import com.letscook.apirecipes.exceptions.BusinessException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.util.*;

import static javax.servlet.SessionTrackingMode.SSL;

@Service
@SuppressWarnings({"squid:S3012", "squid:S1186", "squid:S3776", "squid:S4424"})
public class LetscookService {

    private final ConfigProperties configProperties;

    public LetscookService(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    // Método usado para testar protocolo https ou http evitando bloqueio da api por não ter certificado na plataforma onde executa
    protected OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws
                                CertificateException {
                        }
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws
                                CertificateException {
                        }
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance(String.valueOf(SSL));
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

            builder.hostnameVerifier((hostname, session) -> hostname.equalsIgnoreCase(session.getPeerHost()));

            return builder.build();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    // Get a link de imagem da receita usando a API GIPHY
    protected String getGif(String title) {
        OkHttpClient httpClient = getUnsafeOkHttpClient();
        Response response = null;
        String retorno = "";
        String retornoBody = "";

        try {
            Request requestApiGiphy = new Request.Builder()
                    .url(configProperties.getURL_APIGIPHY() + title +
                            configProperties.getAPI_KEY() +
                            configProperties.getLIMIT() +
                            configProperties.getOFFSET() +
                            configProperties.getRATING() +
                            configProperties.getLANG())
                    .get()
                    .build();

            response = httpClient.newCall(requestApiGiphy).execute();
            retornoBody = response.body().string();

            HashMap<String, Object> map = null;
            TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(retornoBody, typeRef);

            for(Map.Entry<String, Object> entry : map.entrySet()) {

                if (entry.getKey().equals("data")) {
                    ArrayList<Object> arrayList = (ArrayList<Object>) entry.getValue();

                    for(int i = 0; i < arrayList.size(); i++) {
                        LinkedHashMap<String, Object> value = (LinkedHashMap<String, Object>) arrayList.get(i);

                        for (Map.Entry<String, Object> inner : value.entrySet()) {
                            String innerKey = inner.getKey();
                            Object values = inner.getValue();

                            if (innerKey.equals("embed_url")) {
                                retorno = values.toString();
                                break;
                            }
                        }   // End for Map

                        if (!retorno.isEmpty()) {
                            break;
                        }
                    }   // End for arrayList

                }   // End if entry.getKey()

                if (!retorno.isEmpty()) {
                    break;
                }
            }
        } catch (Exception e) {
            retorno = "";
        }

        return retorno;
    }

    // Ordem Alfabetica de um Array
    protected ArrayList<String> sortAlphabetic(Object[] toArray) {
        ArrayList<String> retorno = new ArrayList<>();
        Arrays.sort(toArray);
        for(Object lStr : toArray) {
            retorno.add(lStr.toString().trim());
        }

        return retorno;
    }

    // Get as receitas pela API Recipe Puppy
    public List<DataDTO> getRecipes(String str) {
        OkHttpClient httpClient = getUnsafeOkHttpClient();
        Response response = null;
        List<DataDTO> lista = null;
        ArrayList<JSONObject> arrayListJSONObjects = null;
        DataDTO dto = null;
        JSONObject jsonObject = null;

        String retorno = null;
        String urlGiphy = null;

        Request requestRecipePuppy = new Request.Builder()
                                                .url(configProperties.getURL_APIRECIPEPUPPY() + str +
                                                     configProperties.getLIMIT_RECIPEPUPPY())
                                                .get()
                                                .build();

        try {
            response = httpClient.newCall(requestRecipePuppy).execute();
            retorno = response.body().string();

            HashMap<String, Object> map = null;
            TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(retorno, typeRef);

            for(Map.Entry<String, Object> entry : map.entrySet()) {

                if (entry.getValue() instanceof ArrayList) {
                    ArrayList<Object> arrayList = (ArrayList<Object>) entry.getValue();

                    for(int i = 0; i < arrayList.size(); i++) {
                        LinkedHashMap<String, Object> value = (LinkedHashMap<String, Object>) arrayList.get(i);

                        for (Map.Entry<String, Object> inner : value.entrySet()) {
                            String innerKey = inner.getKey();
                            Object values = inner.getValue();

                            if (jsonObject == null) {
                                jsonObject = new JSONObject();
                            }

                            if (innerKey.equals("title")) {
                                jsonObject.put(innerKey, values.toString().replaceAll("[\\n]", " ").trim());
                                urlGiphy = getGif(values.toString());
                                jsonObject.put("gif", urlGiphy);
                            }
                            if (innerKey.equals("ingredients")) {
                                String[] lStrIngredients = values.toString().split(",");
                                ArrayList<String> listIngredients = new ArrayList<>();
                                for(String lStr : lStrIngredients) {
                                    listIngredients.add(lStr.trim());
                                }

                                // Coloca em ordem alfabetica lista de ingredientes
                                listIngredients = sortAlphabetic(listIngredients.toArray());

                                jsonObject.put(innerKey, listIngredients);
                            }
                            if (innerKey.equals("href")) {
                                jsonObject.put("link", values);
                            }
                            if (innerKey.equals("thumbnail")) {
                                if (arrayListJSONObjects == null) {
                                    arrayListJSONObjects = new ArrayList<>();
                                }
                                arrayListJSONObjects.add(jsonObject);
                                jsonObject = new JSONObject();
                            }
                        }   // End for Map

                    }   // End for arrayList

                }   // End if entry.getValues instanceof ArrayList

            }   // End for Map entry

            if (arrayListJSONObjects != null) {
                dto = new DataDTO();
                ArrayList<String> listKeyWords = new ArrayList<>();
                String[] list = str.split(",");
                for(String lStr : list) {
                    listKeyWords.add(lStr);
                }

                dto.setKeywords(listKeyWords);

                ArrayList<RecipesDTO> listRecipesDTO = null;
                RecipesDTO recipesDTO = null;
                for (int i = 0; i < arrayListJSONObjects.size(); i++) {
                     JSONObject json = arrayListJSONObjects.get(i);
                     recipesDTO = new RecipesDTO();
                     recipesDTO.setTitle(json.getString("title"));

                     JSONArray jsonArray = json.getJSONArray("ingredients");
                     ArrayList<String> listIngredients = new ArrayList<>();
                     for(int j = 0; j < jsonArray.length(); j++) {
                         listIngredients.add(jsonArray.getString(j));
                     }
                     recipesDTO.setIngredients(listIngredients);

                     recipesDTO.setLink(json.getString("link"));
                     recipesDTO.setGif(json.getString("gif"));

                     if (listRecipesDTO == null) {
                         listRecipesDTO = new ArrayList<>();
                     }

                     listRecipesDTO.add(recipesDTO);
                }

                dto.setRecipes(listRecipesDTO);

                if (lista == null) {
                    lista = new ArrayList<>();
                }

                lista.add(dto);
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }

        return lista;
    }

}
