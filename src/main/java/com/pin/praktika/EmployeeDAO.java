package com.pin.praktika;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private Connection connection;

    // Конструктор — устанавливает соединение с БД
    public EmployeeDAO() {
        try {
            String url = "jdbc:postgresql://localhost:5432/Sotrudniki";
            String user = "postgres";
            String password = "admin1234";

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Соединение с БД установлено.");
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД");
            e.printStackTrace();
        }
    }

    // Получить всех сотрудников из БД
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employee";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка выполнения запроса");
            e.printStackTrace();
        }

        return employees;
    }

    // Получить сотрудников, у которых день рождения попадает в указанный период
    public List<Employee> getEmployeesByBirthDatePeriod(LocalDate startDate, int days) {
        List<Employee> employees = new ArrayList<>();
        LocalDate endDate = startDate.plusDays(days);

        String query = "SELECT * FROM Employee WHERE " +
                "(EXTRACT(MONTH FROM birth_date) = ? AND EXTRACT(DAY FROM birth_date) >= ?) OR " +
                "(EXTRACT(MONTH FROM birth_date) = ? AND EXTRACT(DAY FROM birth_date) <= ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, startDate.getMonthValue());
            pstmt.setInt(2, startDate.getDayOfMonth());
            pstmt.setInt(3, endDate.getMonthValue());
            pstmt.setInt(4, endDate.getDayOfMonth());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка фильтрации по дате");
            e.printStackTrace();
        }

        return employees;
    }

    public int insertEmployee(Employee employee) {
        String sql = "INSERT INTO Employee(last_name, first_name, middle_name, birth_date, department, position) VALUES(?,?,?,?,?,?) RETURNING id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee.getLastName());
            pstmt.setString(2, employee.getFirstName());
            pstmt.setString(3, employee.getMiddleName());
            pstmt.setDate(4, Date.valueOf(employee.getBirthDate()));
            pstmt.setString(5, employee.getDepartment());
            pstmt.setString(6, employee.getPosition());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Возвращаем ID нового сотрудника
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении сотрудника");
            e.printStackTrace();
        }

        return -1; // Ошибка
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE Employee SET last_name = ?, first_name = ?, middle_name = ?, birth_date = ?, department = ?, position = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee.getLastName());
            pstmt.setString(2, employee.getFirstName());
            pstmt.setString(3, employee.getMiddleName());
            pstmt.setDate(4, Date.valueOf(employee.getBirthDate()));
            pstmt.setString(5, employee.getDepartment());
            pstmt.setString(6, employee.getPosition());
            pstmt.setInt(7, employee.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении сотрудника");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(int employeeId) {
        String sql = "DELETE FROM Employee WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении сотрудника");
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllDepartments() {
        List<String> departments = new ArrayList<>();
        String sql = "SELECT DISTINCT department FROM Employee WHERE department IS NOT NULL ORDER BY department";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                departments.add(rs.getString("department"));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка загрузки отделов");
            e.printStackTrace();
        }

        return departments;
    }

    public List<String> getAllPositions() {
        List<String> positions = new ArrayList<>();
        String sql = "SELECT DISTINCT position FROM Employee WHERE position IS NOT NULL ORDER BY position";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                positions.add(rs.getString("position"));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка загрузки должностей");
            e.printStackTrace();
        }

        return positions;
    }

    // Вспомогательный метод: преобразование ResultSet в объект Employee
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("id"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getDate("birth_date").toLocalDate(),
                rs.getString("department"),
                rs.getString("position")
        );
    }
}
