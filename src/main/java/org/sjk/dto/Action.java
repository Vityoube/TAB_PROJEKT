package org.sjk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by vkalashnykov on 08.02.17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Action {
    private long id;
    private String name;
    private long userId;
    private long ipId;
    private Timestamp actionTime;
}
