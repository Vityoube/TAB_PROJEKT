package org.sjk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by vkalashnykov on 08.02.17.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Password {
    private long id;
    private String password;
    private long userId;
}
