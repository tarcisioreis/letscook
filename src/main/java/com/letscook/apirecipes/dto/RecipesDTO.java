package com.letscook.apirecipes.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipesDTO implements Serializable {
    private String title;
    private ArrayList<String> ingredients;
    private String link;
    private String gif;
}
