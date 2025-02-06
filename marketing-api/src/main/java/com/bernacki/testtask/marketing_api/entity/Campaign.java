package com.bernacki.testtask.marketing_api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.math.BigInteger;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Size(min = 2, message = "Campaign name is too short")
    @NotBlank(message = "Campaign name must not be empty")
    private String campaignName;

    @DecimalMin(value = "0.15", message = "Bid amount must be higher than 0.15")
    private Double bidAmount;

    private Double campaignFunds;

    @NotNull
    private boolean status;

    @NotNull(message = "Specify the town that you want your campaign to be displayed")
    @ManyToOne
    @JoinColumn(name = "town_id", nullable = false)
    private Town town;

    @NotNull(message = "You have to specify the radius")
    @DecimalMin(value = "1.0", message = "Radius has to be at least 1km")
    private Double radius;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE,
                CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "campaign_keywords",
            joinColumns = @JoinColumn(name = "campaign_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )

    @Size(min = 1)
    @NotNull(message = "You have to specify at least 1 keyword")
    private List<Keyword> keywordList;
}
