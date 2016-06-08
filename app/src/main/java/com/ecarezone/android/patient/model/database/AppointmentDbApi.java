package com.ecarezone.android.patient.model.database;

import android.content.Context;

import com.ecarezone.android.patient.model.Appointment;
import com.ecarezone.android.patient.model.Chat;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by 10603675 on 25-05-2016.
 */
public class AppointmentDbApi {

    private static DbHelper mDbHelper;
    private static AppointmentDbApi mAppointmentDbapi;
    private static Context mContext;

    private AppointmentDbApi() {

    }

    public static AppointmentDbApi getInstance(Context context) {
        mContext = context;
        if (mDbHelper == null || mAppointmentDbapi == null) {
            mDbHelper = new DbHelper(context);
            mAppointmentDbapi = new AppointmentDbApi();
        }
        return mAppointmentDbapi;

    }

    /* Saves a user chat in local database. Returns success failure response. */
    public  boolean saveAppointment(Appointment appointment) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            int status = appointmentDao.create(appointment);
            return status != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* retrieve the AppointmentHistory of a particular user */
    public List<Appointment> getAppointmentHistory(String userId) {
        try {
            Dao<Appointment, Integer> chatDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = chatDao.queryBuilder();
            return queryBuilder.where().eq(DbContract.Appointments.COLUMN_NAME_USER_ID, userId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* retrieves Appointment History of particular user with a particular doctor */
    public boolean getAppointmentHistory(String userId, String doctorId) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            DeleteBuilder<Appointment, Integer> deleteBuilder = appointmentDao.deleteBuilder();
            deleteBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_USER_ID, userId)
                    .and()
                    .eq(DbContract.Appointments.COLUMN_NAME_DOCTOR_ID, doctorId);
            int count = deleteBuilder.delete();
            return count > 0 ? true : false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* update read status of user Chat history */
    public boolean updateAppointment(String userId, String callType, String doctorId, String dateTime,
                                     String appointmentId) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            UpdateBuilder<Appointment, Integer> updateBuilder = appointmentDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_APPOINTMENT_ID, appointmentId);

            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_USER_ID, userId);
            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_CALL_TYPE, callType);
            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_DOCTOR_ID, doctorId);
            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_DATE_TIME, dateTime);

            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Appointment> getAppointmentHistory(long userId, long doctorId, Date startDate) {

        List<Appointment> appointmentList = null;

        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = appointmentDao.queryBuilder();
            queryBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_USER_ID, userId)
                    .and()
                    .eq(DbContract.Appointments.COLUMN_NAME_DOCTOR_ID, doctorId)
                    .and()
                    .gt(DbContract.Appointments.COLUMN_NAME_DATE_TIME, startDate);
            queryBuilder.orderBy(DbContract.Appointments.COLUMN_NAME_DATE_TIME, true);
            appointmentList = queryBuilder.query();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentList;
    }

    /* retrieve appointments from Appointment table by userId */
    public List<Appointment> getAppointments(String userId, boolean isConfirmed) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = appointmentDao.queryBuilder();

            return queryBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_USER_ID, userId)
                    .and()
                    .eq(DbContract.Appointments.COLUMN_NAME_IS_CONFIRMED, isConfirmed)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
