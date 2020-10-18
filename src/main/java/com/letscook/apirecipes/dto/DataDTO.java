package com.letscook.apirecipes.dto;

import lombok.*;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataDTO implements Serializable {
    private ArrayList<String> keywords;
    private ArrayList<RecipesDTO> recipes;
}
