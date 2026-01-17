/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package be.iii.jdbclabo.model;

import java.util.Date;
import java.util.List;

public interface IOrder extends IOrderWithoutDetails {

    List<IOrderDetail> getDetails();

    void setDetails(List<IOrderDetail> details);

}
