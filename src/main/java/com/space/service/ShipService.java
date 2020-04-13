package com.space.service;

import com.space.controller.ShipDisplayOptions;
import com.space.controller.ShipFilter;
import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ShipService {
    private ShipRepository shipRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    private Specification<Ship> filterSpecification(ShipFilter filter){
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                //Если задано имя
                if(filter.getName() != null){
                    predicates.add(
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get("name")),    //Регистронезависимость
                                    "%" + filter.getName().toLowerCase() + "%"
                            )
                    );
                }

                //Если задана планета
                if(filter.getPlanet() != null){
                    predicates.add(
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get("planet")),    //Регистронезависимость
                                    "%" + filter.getPlanet().toLowerCase() + "%"
                            )
                    );
                }

                //Тип корабля
                if(filter.getShipType() != null){
                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("shipType"),
                                    filter.getShipType()
                            )
                    );
                }

                //Год от
                if(filter.getAfter() != null){
                    predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("prodDate"),
                                    new Date(filter.getAfter())
                            )
                    );
                }

                //Год до
                if(filter.getBefore() != null){
                    predicates.add(
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("prodDate"),
                                    new Date(filter.getBefore())
                            )
                    );
                }

                //Бэушный
                if(filter.getUsed() != null){
                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("isUsed"),
                                    filter.getUsed()
                            )
                    );
                }

                //Минимальная скорость
                if(filter.getMinSpeed() != null){
                    predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("speed"),
                                    filter.getMinSpeed()
                            )
                    );
                }

                //Максимальная скорость
                if(filter.getMaxSpeed() != null){
                    predicates.add(
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("speed"),
                                    filter.getMaxSpeed()
                            )
                    );
                }

                //Мин экипаж
                if(filter.getMinCrewSize() != null){
                    predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("crewSize"),
                                    filter.getMinCrewSize()
                            )
                    );
                }

                //Макс экипаж
                if(filter.getMaxCrewSize() != null){
                    predicates.add(
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("crewSize"),
                                    filter.getMaxCrewSize()
                            )
                    );
                }

                //Мин рейтинг
                if(filter.getMinRating() != null){
                    predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("rating"),
                                    filter.getMinRating()
                            )
                    );
                }

                //Макс рейтинг
                if(filter.getMaxRating() != null){
                    predicates.add(
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("rating"),
                                    filter.getMaxRating()
                            )
                    );
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }


    private Specification<Ship> displayOptionSpecification(ShipDisplayOptions shipDisplayOptions){
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                //Если задана сортировка
                if(shipDisplayOptions.getOrder() != null){
                    criteriaQuery.orderBy(
                            criteriaBuilder.asc(
                                    root.get(shipDisplayOptions.getOrder().getFieldName())
                            )
                    );
                }

                return criteriaQuery.getRestriction();
            }
        };
    }

    public Iterable<Ship> retriveShips(ShipFilter filter, ShipDisplayOptions shipDisplayOptions){
        return shipRepository.findAll(filterSpecification(filter).and(displayOptionSpecification(shipDisplayOptions)));
    }
}
