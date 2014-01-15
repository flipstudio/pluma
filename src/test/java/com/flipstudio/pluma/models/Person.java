package com.flipstudio.pluma.models;

import java.util.Date;

/**
 * Created by Pietro Caselani
 * On 14/01/14
 * Pluma
 */
public class Person {
  //region Fields
  private int mId;
  private String mName, mLastName;
  private Date mBirth;
  //endregion

  //region Getters and Setters
  public int getId() {
    return mId;
  }

  public void setId(int id) {
    mId = id;
  }

  public String getName() {
    return mName;
  }

  public void setName(String name) {
    mName = name;
  }

  public String getLastName() {
    return mLastName;
  }

  public void setLastName(String lastName) {
    mLastName = lastName;
  }

  public Date getBirth() {
    return mBirth;
  }

  public void setBirth(Date birth) {
    mBirth = birth;
  }
  //endregion
}